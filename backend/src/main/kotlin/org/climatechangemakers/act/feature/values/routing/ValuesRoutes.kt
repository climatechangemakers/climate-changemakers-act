package org.climatechangemakers.act.feature.values.routing

import io.ktor.application.call
import io.ktor.routing.Route
import io.ktor.routing.get
import org.climatechangemakers.act.feature.values.controller.ValuesController

fun Route.valuesRoutes(controller: ValuesController) {

  get("/areas") { controller.areaValues(call) }
}