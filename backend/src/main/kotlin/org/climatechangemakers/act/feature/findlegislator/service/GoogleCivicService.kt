package org.climatechangemakers.act.feature.findlegislator.service

import org.climatechangemakers.act.feature.findlegislator.model.GoogleCivicApiResult
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleCivicService {

  @GET("representatives")
  suspend fun getCongressionalDistrict(
    @Query("address") address: String,
    @Query("levels") levels: String = "country",
    @Query("roles") roles: String = "legislatorLowerBody",
    @Query("includeOffices") includeOffices: Boolean = false,
  ): GoogleCivicApiResult
}