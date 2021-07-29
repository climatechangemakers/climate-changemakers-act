package com.climatechangemakers.act.feature.findlegislator.service

import com.climatechangemakers.act.feature.findlegislator.model.GeocodioApiResult
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodioService {

  @GET("geocode")
  suspend fun geocode(
    @Query("q") query: String,
    @Query("fields") fields: List<String> = listOf("cd"),
  ): GeocodioApiResult
}