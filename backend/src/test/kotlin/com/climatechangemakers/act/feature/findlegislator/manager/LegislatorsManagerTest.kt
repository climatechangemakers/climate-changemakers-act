package com.climatechangemakers.act.feature.findlegislator.manager

import com.climatechangemakers.act.feature.findlegislator.model.CongressionalDistrict
import com.climatechangemakers.act.feature.findlegislator.model.Fields
import com.climatechangemakers.act.feature.findlegislator.model.GeocodeResult
import com.climatechangemakers.act.feature.findlegislator.model.GeocodioApiResult
import com.climatechangemakers.act.feature.findlegislator.model.GeocodioLegislator
import com.climatechangemakers.act.feature.findlegislator.model.GeocodioPoliticalParty
import com.climatechangemakers.act.feature.findlegislator.model.GetLegislatorsByAddressRequest
import com.climatechangemakers.act.feature.findlegislator.model.Legislator
import com.climatechangemakers.act.feature.findlegislator.model.LegislatorArea
import com.climatechangemakers.act.feature.findlegislator.model.LegislatorBio
import com.climatechangemakers.act.feature.findlegislator.model.LegislatorContactInformation
import com.climatechangemakers.act.feature.findlegislator.model.LegislatorPoliticalParty
import com.climatechangemakers.act.feature.findlegislator.model.LegislatorReferences
import com.climatechangemakers.act.feature.findlegislator.model.LegislatorRole
import com.climatechangemakers.act.feature.findlegislator.model.Location
import com.climatechangemakers.act.feature.findlegislator.service.FakeGeocodioService
import com.climatechangemakers.act.feature.findlegislator.util.suspendTest
import com.climatechangemakers.act.feature.lcvscore.manager.LcvScoreManager
import com.climatechangemakers.act.feature.lcvscore.model.LcvScore
import com.climatechangemakers.act.feature.lcvscore.model.LcvScoreType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LegislatorsManagerTest {

  private val fakeLcvManager = object : LcvScoreManager {
    override suspend fun getLifetimeScore(
      bioguideId: String
    ) = LcvScore(10, LcvScoreType.LifetimeScore)

    override suspend fun getYearlyScores(bioguideId: String) = listOf(
      LcvScore(10, LcvScoreType.YearlyScore(2020)),
      LcvScore(10, LcvScoreType.YearlyScore(2019)),
    )
  }

  private val fakeDistrictOfficerManager = DistrictOfficerManager { _, _ -> "867-5309" }

  private val fakeGeocodioService = FakeGeocodioService {
    GeocodioApiResult(
      results = listOf(
        GeocodeResult(
          location = Location(0.0, 0.0),
          fields = Fields(
            congressionalDistricts = listOf(
              CongressionalDistrict(
                "VA_04",
                4,
                currentLegislators = listOf(
                  GeocodioLegislator(
                    type = LegislatorRole.Representative,
                    bio = LegislatorBio("McEachin", "A. Donald", GeocodioPoliticalParty.Democrat),
                    contactInfo = LegislatorContactInformation(
                      siteUrl = "www.foo.com",
                      formattedAddress = "foo",
                      phone = "555-555-5555",
                    ),
                    references = LegislatorReferences(bioguide = "M00001")
                  ),
                  GeocodioLegislator(
                    type = LegislatorRole.Senator,
                    bio = LegislatorBio("Kaine", "Tim", GeocodioPoliticalParty.Republican),
                    contactInfo = LegislatorContactInformation(
                      siteUrl = "www.foo.com",
                      formattedAddress = "foo",
                      phone = "555-555-5555",
                    ),
                    references = LegislatorReferences(bioguide = "M00001")
                  )
                )
              )
            )
          )
        )
      )
    )
  }

  private val manager = LegislatorsManager(fakeGeocodioService, fakeLcvManager, fakeDistrictOfficerManager)

  @Test fun `getLegislators gets called with correct query string`() = suspendTest {
    val request = GetLegislatorsByAddressRequest(
      streetAddress = "10 Beech Place",
      city = "West Deptford",
      state = "NJ",
      postalCode = "08096",
    )

    manager.getLegislators(request)

    assertEquals(
      expected = "10 Beech Place, West Deptford NJ 08096",
      actual = fakeGeocodioService.capturedQuery
    )
  }

  @Test fun `getLegislators maps correctly to domain legislator type`() = suspendTest {
    val request = GetLegislatorsByAddressRequest(
      streetAddress = "10 Beech Place",
      city = "West Deptford",
      state = "NJ",
      postalCode = "08096",
    )

    val response = manager.getLegislators(request)

    assertEquals(
      expected = listOf(
        Legislator(
          name = "A. Donald McEachin",
          role = LegislatorRole.Representative,
          siteUrl = "www.foo.com",
          phoneNumbers = listOf("555-555-5555", "867-5309"),
          imageUrl = "https://bioguide.congress.gov/bioguide/photo/M/M00001.jpg",
          lcvScores = listOf(
            LcvScore(10, LcvScoreType.LifetimeScore),
            LcvScore(10, LcvScoreType.YearlyScore(2020)),
            LcvScore(10, LcvScoreType.YearlyScore(2019)),
          ),
          area = LegislatorArea("NJ", 4),
          partyAffiliation = LegislatorPoliticalParty.Democrat,
        ),
        Legislator(
          name = "Tim Kaine",
          role = LegislatorRole.Senator,
          siteUrl = "www.foo.com",
          phoneNumbers = listOf("555-555-5555", "867-5309"),
          imageUrl = "https://bioguide.congress.gov/bioguide/photo/M/M00001.jpg",
          lcvScores = listOf(
            LcvScore(10, LcvScoreType.LifetimeScore),
            LcvScore(10, LcvScoreType.YearlyScore(2020)),
            LcvScore(10, LcvScoreType.YearlyScore(2019)),
          ),
          area = LegislatorArea("NJ", null),
          partyAffiliation = LegislatorPoliticalParty.Republican,
        ),
      ),
      actual = response
    )
  }

  @Test fun `getLegislators throws IllegalStateException with wrong LCV scores`() = suspendTest {
    val request = GetLegislatorsByAddressRequest(
      streetAddress = "10 Beech Place",
      city = "West Deptford",
      state = "NJ",
      postalCode = "08096",
    )

    val manager = LegislatorsManager(
      geocodioService = fakeGeocodioService,
      lcvScoreManager = object : LcvScoreManager {
        override suspend fun getLifetimeScore(bioguideId: String): LcvScore? = null
        override suspend fun getYearlyScores(bioguideId: String): List<LcvScore> = emptyList()
      },
      fakeDistrictOfficerManager,
    )

    assertFailsWith<IllegalStateException> {
      manager.getLegislators(request)
    }
  }
}