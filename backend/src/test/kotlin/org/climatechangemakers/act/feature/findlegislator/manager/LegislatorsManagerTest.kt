package org.climatechangemakers.act.feature.findlegislator.manager

import org.climatechangemakers.act.common.model.RepresentedArea
import org.climatechangemakers.act.feature.findlegislator.model.CongressionalDistrict
import org.climatechangemakers.act.feature.findlegislator.model.Fields
import org.climatechangemakers.act.feature.findlegislator.model.GeocodeResult
import org.climatechangemakers.act.feature.findlegislator.model.GeocodioApiResult
import org.climatechangemakers.act.feature.findlegislator.model.GeocodioLegislator
import org.climatechangemakers.act.feature.findlegislator.model.GetLegislatorsByAddressRequest
import org.climatechangemakers.act.feature.findlegislator.model.Legislator
import org.climatechangemakers.act.feature.findlegislator.model.LegislatorArea
import org.climatechangemakers.act.feature.findlegislator.model.LegislatorPoliticalParty
import org.climatechangemakers.act.feature.findlegislator.model.LegislatorReferences
import org.climatechangemakers.act.feature.findlegislator.model.LegislatorRole
import org.climatechangemakers.act.feature.findlegislator.model.Location
import org.climatechangemakers.act.feature.findlegislator.model.MemberOfCongress
import org.climatechangemakers.act.feature.findlegislator.service.FakeGeocodioService
import org.climatechangemakers.act.feature.findlegislator.util.suspendTest
import org.climatechangemakers.act.feature.lcvscore.manager.LcvScoreManager
import org.climatechangemakers.act.feature.lcvscore.model.LcvScore
import org.climatechangemakers.act.feature.lcvscore.model.LcvScoreType
import kotlin.test.Test
import kotlin.test.assertEquals

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

  private val fakeMemberOfCongressManager = MemberOfCongressManager { bioguideId ->
    when (bioguideId) {
      "M00001" -> MemberOfCongress(
        bioguideId = bioguideId,
        fullName = "A. Donald McEachin",
        legislativeRole = LegislatorRole.Representative,
        representedArea = RepresentedArea.NewJersey,
        congressionalDistrict = 4,
        party = LegislatorPoliticalParty.Democrat,
        dcPhoneNumber = "555-555-5555",
        twitterHandle = "fancytwitter",
        cwcOfficeCode = "foo",
      )
      "K00001" -> MemberOfCongress(
        bioguideId = bioguideId,
        fullName = "Tim Kaine",
        legislativeRole = LegislatorRole.Senator,
        representedArea = RepresentedArea.NewJersey,
        congressionalDistrict = null,
        party = LegislatorPoliticalParty.Republican,
        dcPhoneNumber = "555-555-5555",
        twitterHandle = "fancytwitter2",
        cwcOfficeCode = "foo",
      )
      else -> error("")
    }
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
                currentLegislators = listOf(
                  GeocodioLegislator(references = LegislatorReferences(bioguide = "M00001")),
                  GeocodioLegislator(references = LegislatorReferences(bioguide = "K00001"))
                )
              )
            )
          )
        )
      )
    )
  }

  private val manager = LegislatorsManager(
    fakeGeocodioService,
    fakeLcvManager,
    fakeDistrictOfficerManager,
    fakeMemberOfCongressManager,
  )

  @Test fun `getLegislators gets called with correct query string`() = suspendTest {
    val request = GetLegislatorsByAddressRequest(
      streetAddress = "10 Beech Place",
      city = "West Deptford",
      state = RepresentedArea.NewJersey,
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
      state = RepresentedArea.NewJersey,
      postalCode = "08096",
    )

    val response = manager.getLegislators(request)

    assertEquals(
      expected = listOf(
        Legislator(
          name = "A. Donald McEachin",
          bioguideId = "M00001",
          role = LegislatorRole.Representative,
          phoneNumbers = listOf("555-555-5555", "867-5309"),
          twitter = "fancytwitter",
          imageUrl = "https://bioguide.congress.gov/bioguide/photo/M/M00001.jpg",
          lcvScores = listOf(
            LcvScore(10, LcvScoreType.LifetimeScore),
            LcvScore(10, LcvScoreType.YearlyScore(2020)),
            LcvScore(10, LcvScoreType.YearlyScore(2019)),
          ),
          area = LegislatorArea(RepresentedArea.NewJersey, 4),
          partyAffiliation = LegislatorPoliticalParty.Democrat,
        ),
        Legislator(
          name = "Tim Kaine",
          bioguideId = "K00001",
          role = LegislatorRole.Senator,
          phoneNumbers = listOf("555-555-5555", "867-5309"),
          twitter = "fancytwitter2",
          imageUrl = "https://bioguide.congress.gov/bioguide/photo/K/K00001.jpg",
          lcvScores = listOf(
            LcvScore(10, LcvScoreType.LifetimeScore),
            LcvScore(10, LcvScoreType.YearlyScore(2020)),
            LcvScore(10, LcvScoreType.YearlyScore(2019)),
          ),
          area = LegislatorArea(RepresentedArea.NewJersey, null),
          partyAffiliation = LegislatorPoliticalParty.Republican,
        ),
      ),
      actual = response
    )
  }
}
