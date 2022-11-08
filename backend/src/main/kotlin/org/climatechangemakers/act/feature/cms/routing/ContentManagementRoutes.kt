package org.climatechangemakers.act.feature.cms.routing

import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.contentManagementRoutes() = cmsAuthenticated {
  get("/cms") {
    call.respondText("Hello World!")
  }
}