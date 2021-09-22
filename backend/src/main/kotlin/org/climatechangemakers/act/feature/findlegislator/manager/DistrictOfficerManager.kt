package org.climatechangemakers.act.feature.findlegislator.manager

import org.climatechangemakers.act.feature.findlegislator.model.Location

fun interface DistrictOfficerManager {

  suspend fun getNearestDistrictOfficePhoneNumber(
    bioguideId: String,
    requestingLocation: Location
  ): String?
}