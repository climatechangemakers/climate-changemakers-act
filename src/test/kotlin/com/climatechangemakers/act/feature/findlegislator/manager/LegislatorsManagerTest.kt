package com.climatechangemakers.act.feature.findlegislator.manager

import com.climatechangemakers.act.feature.congressgov.manager.SearchCongressManager
import com.climatechangemakers.act.feature.findlegislator.model.CongressionalDistrict
import com.climatechangemakers.act.feature.findlegislator.model.Fields
import com.climatechangemakers.act.feature.findlegislator.model.GeocodeResult
import com.climatechangemakers.act.feature.findlegislator.model.GeocodioApiResult
import com.climatechangemakers.act.feature.findlegislator.model.GeocodioLegislator
import com.climatechangemakers.act.feature.findlegislator.model.GetLegislatorsRequest
import com.climatechangemakers.act.feature.findlegislator.model.Legislator
import com.climatechangemakers.act.feature.findlegislator.model.LegislatorBio
import com.climatechangemakers.act.feature.findlegislator.model.LegislatorContactInformation
import com.climatechangemakers.act.feature.findlegislator.model.LegislatorType
import com.climatechangemakers.act.feature.findlegislator.service.FakeGeocodioService
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

  private val fakeGeocodioService = FakeGeocodioService {
    GeocodioApiResult(
      results = listOf(
        GeocodeResult(
          fields = Fields(
            congressionalDistricts = listOf(
              CongressionalDistrict(
                "VA-04",
                4,
                currentLegislators = listOf(
                  GeocodioLegislator(
                    LegislatorType.Representative,
                    LegislatorBio("McEachin", "A."),
                    LegislatorContactInformation(
                      "www.foo.com",
                      "This is an address.",
                      "555-555-5555"
                    )
                  )
                )
              )
            )
          )
        )
      )
    )
  }

  private val fakeSearchCongressManager = SearchCongressManager { "www.bar.com" }
  private val manager = LegislatorsManager(fakeGeocodioService, fakeLcvManager, fakeSearchCongressManager)

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
      actual = fakeGeocodioService.capturedQuery
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
        name = "A. McEachin",
        type = LegislatorType.Representative,
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
      geocodioService = fakeGeocodioService,
      lcvScoreManager = { emptyList() },
      searchCongressManager = fakeSearchCongressManager,
    )

    assertFailsWith<IllegalStateException> {
      manager.getLegislators(request)
    }
  }
}