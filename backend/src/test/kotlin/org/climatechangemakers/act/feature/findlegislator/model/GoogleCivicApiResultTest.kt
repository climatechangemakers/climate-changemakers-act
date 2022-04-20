package org.climatechangemakers.act.feature.findlegislator.model

import org.junit.Test
import kotlin.test.assertEquals

class GoogleCivicApiResultTest {

  @Test fun `civic api result can parse single digit congressional division from open civic data format`() {
    val formatted = "ocd-division/country:us/state:va/cd:4"
    val actual = GoogleCivicApiResult(mapOf(formatted to emptyMap())).congressionalDistrict
    assertEquals(4, actual)
  }

  @Test fun `civic api result can parse single digit congressional division leading zero from open civic data format`() {
    val formatted = "ocd-division/country:us/state:va/cd:04"
    val actual = GoogleCivicApiResult(mapOf(formatted to emptyMap())).congressionalDistrict
    assertEquals(4, actual)
  }

  @Test fun `civic api result can parse double digit congressional division from open civic data format`() {
    val formatted = "ocd-division/country:us/state:va/cd:10"
    val actual = GoogleCivicApiResult(mapOf(formatted to emptyMap())).congressionalDistrict
    assertEquals(10, actual)
  }

  @Test fun `civic api result can parse ocd result without congressional district`() {
    val formatted = "ocd-division/country:us/state:sd"
    val actual = GoogleCivicApiResult(mapOf(formatted to emptyMap())).congressionalDistrict
    assertEquals(0, actual)
  }
}