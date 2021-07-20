package com.climatechangemakers.act.feature.representativefinder.routing

import com.climatechangemakers.act.feature.representativefinder.controller.RepresentativeController
import com.climatechangemakers.act.feature.representativefinder.manager.LegislatorFinderManager
import com.climatechangemakers.act.feature.representativefinder.model.GetLegislatorsRequest
import io.ktor.application.call
import io.ktor.http.Parameters
import io.ktor.request.receive
import io.ktor.request.receiveParameters
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post

fun Routing.legislatorRoutes(controller: RepresentativeController) {

  post("/legislators") { controller.hanleLegislators(call) }
}