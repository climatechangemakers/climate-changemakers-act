package org.climatechangemakers.act.feature.findlegislator.model

import kotlinx.serialization.Serializable

private val openCivicDataRegex = """ocd-division/country:us/state:[a-zA-Z]{2}/cd:(\d+)""".toRegex()

@Serializable class GoogleCivicApiResult(
  private val divisions: Map<String, Map<String, String>>,
) {
  val congressionalDistrict: Short get() = openCivicDataRegex
    .matchEntire(divisions.keys.first())
    ?.groups
    ?.get(1)
    ?.value
    ?.toShort()
    ?: 0 // If no match is present, then this state has an "at large" congressional district.
}
