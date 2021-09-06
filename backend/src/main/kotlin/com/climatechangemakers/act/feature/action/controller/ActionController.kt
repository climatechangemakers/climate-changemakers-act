package com.climatechangemakers.act.feature.action.controller

import com.climatechangemakers.act.feature.action.manager.ActionTrackerManager
import com.climatechangemakers.act.feature.action.model.InitiateActionRequest
import com.climatechangemakers.act.feature.action.model.InitiateActionResponse
import com.climatechangemakers.act.feature.findlegislator.manager.LegislatorsManager
import com.climatechangemakers.act.feature.findlegislator.model.GetLegislatorsByAddressRequest
import io.ktor.application.ApplicationCall
import io.ktor.request.receive
import io.ktor.response.respond
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class ActionController @Inject constructor(
  private val legislatorsManager: LegislatorsManager,
  private val actionTrackerManager: ActionTrackerManager,
) {

  suspend fun initiateAction(call: ApplicationCall) = coroutineScope {
    val request = call.receive<InitiateActionRequest>()
    val response = async {
      InitiateActionResponse(
        request.email,
        legislatorsManager.getLegislators(request.toGetLegislatorsRequest())
      )
    }

    launch { actionTrackerManager.trackActionInitiated(request.email) }
    call.respond(response.await())
  }
}

private fun InitiateActionRequest.toGetLegislatorsRequest() = GetLegislatorsByAddressRequest(
  streetAddress = streetAddress,
  city = city,
  state = state,
  postalCode = postalCode,
)