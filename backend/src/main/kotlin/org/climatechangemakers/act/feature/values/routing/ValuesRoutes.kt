package org.climatechangemakers.act.feature.values.routing

import io.ktor.server.application.call
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import org.climatechangemakers.act.feature.values.controller.ValuesController

fun Route.valuesRoutes(controller: ValuesController) = route("/values") {

  get("/areas") { controller.areaValues(call) }
  get("/library-of-congress-topics") { controller.libraryOfCongressTopicValues(call) }
  get("/prefixes") { controller.prefixValues(call) }
}