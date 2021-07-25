package com.climatechangemakers.act.feature.findlegislator.service

import com.climatechangemakers.act.feature.findlegislator.model.GoogleCivicInformationResponse
import retrofit2.http.GET
import retrofit2.http.Query

fun interface GoogleCivicInformationService {

  @GET("/civicinfo/v2/representatives?includeOfficials=true&levels=country&roles=legislatorUpperBody&roles=legislatorLowerBody")
  suspend fun getLegislators(
    @Query("address") address: String,
  ): GoogleCivicInformationResponse
}