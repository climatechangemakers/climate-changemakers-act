package com.climatechangemakers.act.feature.findlegislator.manager

import com.climatechangemakers.act.feature.findlegislator.model.GetLegislatorsRequest
import com.climatechangemakers.act.feature.findlegislator.model.GoogleCivicInformationResponse
import com.climatechangemakers.act.feature.findlegislator.model.GoogleCivicLegislator
import com.climatechangemakers.act.feature.findlegislator.model.GoogleCivicOffice
import com.climatechangemakers.act.feature.findlegislator.model.Legislator
import com.climatechangemakers.act.feature.findlegislator.model.LegislatorRole
import com.climatechangemakers.act.feature.findlegislator.service.FakeGoogleCivicInformationService
import com.climatechangemakers.act.feature.findlegislator.util.suspendTest
import com.climatechangemakers.act.feature.lcvscore.manager.LcvScoreManager
import com.climatechangemakers.act.feature.lcvscore.model.LcvScore
import com.climatechangemakers.act.feature.lcvscore.model.LcvScoreType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LegislatorsManagerTest {

  private val fakeLcvManager = LcvScoreManager { listOf(
    LcvScore(10, LcvScoreType.LifetimeScore),
    LcvScore(10, LcvScoreType.YearlyScore(2020)),
    LcvScore(10, LcvScoreType.YearlyScore(2019)),
  ) }

  private val fakeGoogleCivicService = FakeGoogleCivicInformationService {
    GoogleCivicInformationResponse(
      offices = listOf(
        GoogleCivicOffice(role = LegislatorRole.Representative, legislatorIndices = listOf(0))
      ),
      legislators = listOf(
        GoogleCivicLegislator(
          name = "A. Donald McEachin",
          phoneNumbers = listOf("555-555-5555"),
          urls = listOf("www.foo.com"),
          photoUrl = "www.bar.com"
        )
      )
    )
  }

  private val manager = LegislatorsManager(fakeGoogleCivicService, fakeLcvManager)

  @Test fun `getLegislators gets called with correct query string`() = suspendTest {
    val request = GetLegislatorsRequest(
      streetAddress = "10 Beech Place",
      city = "West Deptford",
      state = "NJ",
      postalCode = "08096",
    )

    manager.getLegislators(request)

    assertEquals(
      expected = "10 Beech Place, West Deptford NJ 08096",
      actual = fakeGoogleCivicService.capturedQuery
    )
  }

  @Test fun `getLegislators maps correctly to domain legislator type`() = suspendTest {
    val request = GetLegislatorsRequest(
      streetAddress = "10 Beech Place",
      city = "West Deptford",
      state = "NJ",
      postalCode = "08096",
    )

    val response = manager.getLegislators(request)

    assertEquals(
      expected = listOf(Legislator(
        name = "A. Donald McEachin",
        role = LegislatorRole.Representative,
        siteUrl = "www.foo.com",
        phone = "555-555-5555",
        imageUrl = "www.bar.com",
        lcvScores = listOf(
          LcvScore(10, LcvScoreType.LifetimeScore),
          LcvScore(10, LcvScoreType.YearlyScore(2020)),
          LcvScore(10, LcvScoreType.YearlyScore(2019)),
        ),
      )),
      actual = response
    )
  }

  @Test fun `getLegislators throws IllegalStateException with wrong LCV scores`() = suspendTest {
    val request = GetLegislatorsRequest(
      streetAddress = "10 Beech Place",
      city = "West Deptford",
      state = "NJ",
      postalCode = "08096",
    )

    val manager = LegislatorsManager(
      civicService = fakeGoogleCivicService,
      lcvScoreManager = { emptyList() },
    )

    assertFailsWith<IllegalStateException> {
      manager.getLegislators(request)
    }
  }

  @Test fun `getLegislators throws IllegalStateException with no associated legislator role`() = suspendTest {
    val request = GetLegislatorsRequest(
      streetAddress = "10 Beech Place",
      city = "West Deptford",
      state = "NJ",
      postalCode = "08096",
    )

    val fakeCivicClient = FakeGoogleCivicInformationService {
      GoogleCivicInformationResponse(
        offices = emptyList(),
        legislators = listOf(
          GoogleCivicLegislator(
            name = "A. Donald McEachin",
            phoneNumbers = listOf("555-555-5555"),
            urls = listOf("www.foo.com"),
            photoUrl = "www.bar.com"
          )
        )
      )
    }

    val manager = LegislatorsManager(
      civicService = fakeCivicClient,
      lcvScoreManager = { emptyList() },
    )

    assertFailsWith<IllegalStateException> {
      manager.getLegislators(request)
    }
  }
}