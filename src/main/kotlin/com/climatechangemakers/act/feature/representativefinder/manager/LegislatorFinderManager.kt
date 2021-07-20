package com.climatechangemakers.act.feature.representativefinder.manager

import com.climatechangemakers.act.feature.representativefinder.model.GetLegislatorsRequest
import com.climatechangemakers.act.feature.representativefinder.model.GetLegislatorsResponse
import com.climatechangemakers.act.feature.representativefinder.service.GeocodioService
import javax.inject.Inject

class LegislatorFinderManager @Inject constructor(
  private val client: GeocodioService
) {

  suspend fun getLegislators(request: GetLegislatorsRequest): GetLegislatorsResponse {
    val geoCodioResponse = client.getLegislators(
      query = request.queryString,
    )

    TODO()
  }
}

private val GetLegislatorsRequest.queryString: String get() = "$streetAddress, $city $state $postalCode"