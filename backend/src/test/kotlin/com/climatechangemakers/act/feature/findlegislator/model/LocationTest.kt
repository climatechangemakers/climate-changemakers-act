package com.climatechangemakers.act.feature.findlegislator.model

import org.junit.Test
import kotlin.test.assertEquals

class LocationTest {

  @Test fun `distance calculation gives proper amount`() {
    val location1 = Location(40.7486, -73.9864)
    val location2 = Location(33.111, -64.7654)

    assertEquals(1179.0, location1.distanceBetween(location2), absoluteTolerance = .1)
  }
}