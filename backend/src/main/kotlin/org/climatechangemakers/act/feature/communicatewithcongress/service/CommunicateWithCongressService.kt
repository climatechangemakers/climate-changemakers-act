package org.climatechangemakers.act.feature.communicatewithcongress.service

import org.climatechangemakers.act.feature.communicatewithcongress.model.CommunicateWithCogressRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface CommunicateWithCongressService {

  @POST(".") suspend fun contact(@Body request: CommunicateWithCogressRequest)
}