package com.climatechangemakers.act.feature.representativefinder.manager

import com.climatechangemakers.act.feature.representativefinder.model.CongressionalDistrict
import com.climatechangemakers.act.feature.representativefinder.model.Fields
import com.climatechangemakers.act.feature.representativefinder.model.GeocodeResult
import com.climatechangemakers.act.feature.representativefinder.model.GeocodioApiResult
import com.climatechangemakers.act.feature.representativefinder.model.GeocodioLegislator
import com.climatechangemakers.act.feature.representativefinder.model.GetRepresentativeRequest
import com.climatechangemakers.act.feature.representativefinder.model.Legislator
import com.climatechangemakers.act.feature.representativefinder.model.LegislatorBio
import com.climatechangemakers.act.feature.representativefinder.model.LegislatorContactInformation
import com.climatechangemakers.act.feature.representativefinder.model.LegislatorType
import com.climatechangemakers.act.feature.representativefinder.service.FakeGeocodioService
import com.climatechangemakers.act.feature.representativefinder.util.suspendTest
import kotlin.test.Test
import kotlin.test.assertEquals

class TestLegislatorsManager {

  private val service = FakeGeocodioService {
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

  private val manager = LegislatorsManager(service)

  @Test fun `getLegislators gets called with correct query string`() = suspendTest {
    val request = GetRepresentativeRequest(
      streetAddress = "10 Beech Place",
      city = "West Deptford",
      state = "NJ",
      postalCode = "08096",
    )

    manager.getLegislators(request)

    assertEquals(
      expected = "10 Beech Place, West Deptford NJ 08096",
      actual = service.capturedQuery
    )
  }

  @Test fun `getLegislators maps correctly to domain legislator type`() = suspendTest {
    val request = GetRepresentativeRequest(
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
      )),
      actual = response
    )
  }
}