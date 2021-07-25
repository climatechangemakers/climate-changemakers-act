package com.climatechangemakers.act.feature.action.controller

import com.climatechangemakers.act.feature.action.model.InitiateActionRequest
import com.climatechangemakers.act.feature.action.model.InitiateActionResponse
import com.climatechangemakers.act.feature.findlegislator.manager.LegislatorsManager
import com.climatechangemakers.act.feature.findlegislator.model.GetLegislatorsRequest
import io.ktor.application.ApplicationCall
import io.ktor.request.receive
import io.ktor.response.respond
import javax.inject.Inject

class ActionController @Inject constructor(
  private val manager: LegislatorsManager,
) {

  suspend fun initiateAction(call: ApplicationCall) {
    val request = call.receive<InitiateActionRequest>()
    call.respond(
      InitiateActionResponse(
        request.email,
        manager.getLegislators(request.toGetLegislatorsRequest())
      )
    )
  }
}

private fun InitiateActionRequest.toGetLegislatorsRequest() = GetLegislatorsRequest(
  streetAddress = streetAddress,
  city = city,
  state = state,
  postalCode = postalCode,
)