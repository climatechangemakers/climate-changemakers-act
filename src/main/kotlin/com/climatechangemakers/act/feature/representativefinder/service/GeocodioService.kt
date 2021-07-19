package com.climatechangemakers.act.feature.representativefinder.service

import com.climatechangemakers.act.feature.representativefinder.model.GetLegislatorsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodioService {

  /**
   * TODO(kcianfarini) embed the api key on every request
   * https://futurestud.io/tutorials/retrofit-2-how-to-add-query-parameters-to-every-request
   */
  @GET("geocode?fields=cd")
  suspend fun getLegislators(
    @Query("q") query: String,
  ): GetLegislatorsResponse
}