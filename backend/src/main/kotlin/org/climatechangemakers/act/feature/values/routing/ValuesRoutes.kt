package org.climatechangemakers.act.feature.values.routing

import io.ktor.application.call
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.route
import org.climatechangemakers.act.feature.values.controller.ValuesController

fun Route.valuesRoutes(controller: ValuesController) = route("/values") {

  get("/areas") { controller.areaValues(call) }
  get("/library-of-congress-topics") { controller.libraryOfCongressTopicValues(call) }
  get("/prefixes") { controller.prefixValues(call) }
}