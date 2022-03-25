package org.climatechangemakers.act.feature.findlegislator.manager

import org.climatechangemakers.act.common.model.RepresentedArea
import org.climatechangemakers.act.feature.findlegislator.model.GeocodeResult
import org.climatechangemakers.act.feature.findlegislator.model.GeocodioApiResult
import org.climatechangemakers.act.feature.findlegislator.model.GetLegislatorsByAddressRequest
import org.climatechangemakers.act.feature.findlegislator.model.MemberOfCongressDto
import org.climatechangemakers.act.feature.findlegislator.model.LegislatorArea
import org.climatechangemakers.act.feature.findlegislator.model.LegislatorPoliticalParty
import org.climatechangemakers.act.feature.findlegislator.model.LegislatorRole
import org.climatechangemakers.act.feature.findlegislator.model.Location
import org.climatechangemakers.act.feature.findlegislator.service.FakeGeocodioService
import org.climatechangemakers.act.feature.findlegislator.service.FakeGoogleCivicService
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

  private val fakeGoogleCivicService = FakeGoogleCivicService()
  private val fakeMemberOfCongressManager = FakeMemberOfCongressManager()
  private val fakeDistrictOfficerManager = DistrictOfficerManager { _, _ -> "867-5309" }
  private val fakeGeocodioService = FakeGeocodioService {
    GeocodioApiResult(
      listOf(
        GeocodeResult(Location(0.0, 0.0))
      )
    )
  }

  private val manager = LegislatorsManager(
    fakeGeocodioService,
    fakeGoogleCivicService,
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

    fakeGoogleCivicService.resultQueue.send(FakeGoogleCivicService.buildApiResponse(RepresentedArea.Virginia, 4))
    fakeMemberOfCongressManager.memberListQueue.send(listOf(FakeMemberOfCongressManager.DEFAULT_MEMBER))
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

    fakeGoogleCivicService.resultQueue.send(FakeGoogleCivicService.buildApiResponse(RepresentedArea.Virginia, 4))
    fakeMemberOfCongressManager.memberListQueue.send(listOf(FakeMemberOfCongressManager.DEFAULT_MEMBER))
    val response = manager.getLegislators(request)

    assertEquals(
      expected = listOf(
        MemberOfCongressDto(
          name = "Full name",
          bioguideId = "bioguide",
          role = LegislatorRole.Representative,
          phoneNumbers = listOf("(555) 555-5555", "867-5309"),
          twitter = "foo",
          imageUrl = "https://bioguide.congress.gov/bioguide/photo/b/bioguide.jpg",
          area = LegislatorArea(RepresentedArea.Virginia, 1),
          lcvScores = listOf(
            LcvScore(10, LcvScoreType.LifetimeScore),
            LcvScore(10, LcvScoreType.YearlyScore(2020)),
            LcvScore(10, LcvScoreType.YearlyScore(2019)),
          ),
          partyAffiliation = LegislatorPoliticalParty.Republican,
        ),
      ),
      actual = response
    )
  }
}
