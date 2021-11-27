package org.climatechangemakers.act.feature.membership.service

import org.climatechangemakers.act.feature.membership.model.AirtableResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface AirtableService {

  @GET(".") suspend fun checkMembership(
    @Query("filterByFormula") formula: AirtableFormula.FilterByEmailFormula
  ): AirtableResponse
}