package com.climatechangemakers.act.feature.action.controller

import com.climatechangemakers.act.feature.representativefinder.manager.LegislatorsManager
import com.climatechangemakers.act.feature.representativefinder.model.GetRepresentativeRequest
import io.ktor.application.ApplicationCall
import io.ktor.request.receive
import io.ktor.response.respond
import javax.inject.Inject

class ActionController @Inject constructor(
  private val manager: LegislatorsManager,
) {

  suspend fun initiateAction(call: ApplicationCall) {
    val request = call.receive<GetRepresentativeRequest>()
    call.respond(manager.getLegislators(request))
  }
}