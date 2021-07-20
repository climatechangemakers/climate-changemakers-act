package com.climatechangemakers.act.feature.representativefinder.controller

import com.climatechangemakers.act.feature.representativefinder.manager.LegislatorFinderManager
import com.climatechangemakers.act.feature.representativefinder.model.GetLegislatorsRequest
import io.ktor.application.ApplicationCall
import io.ktor.request.receive
import io.ktor.response.respond
import javax.inject.Inject

class RepresentativeController @Inject constructor(
  private val manager: LegislatorFinderManager,
) {

  suspend fun hanleLegislators(call: ApplicationCall) {
    val request = call.receive<GetLegislatorsRequest>()
    call.respond(manager.getLegislators(request))
  }
}