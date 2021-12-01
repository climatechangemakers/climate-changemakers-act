package org.climatechangemakers.act.feature.membership.service

import org.climatechangemakers.act.feature.membership.model.AirtableCreateRecordRequest
import org.climatechangemakers.act.feature.membership.model.AirtableResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AirtableService {

  @GET(".") suspend fun checkMembership(
    @Query("filterByFormula") formula: AirtableFormula.FilterByEmailFormula
  ): Response<AirtableResponse>

  @POST(".") suspend fun signUp(
    @Body body: AirtableCreateRecordRequest,
  ): Response<Unit>
}