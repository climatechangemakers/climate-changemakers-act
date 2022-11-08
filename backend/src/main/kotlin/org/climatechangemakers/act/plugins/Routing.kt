package org.climatechangemakers.act.plugins

import org.climatechangemakers.act.feature.action.routing.actionRoutes
import org.climatechangemakers.act.feature.issue.routing.issueRoutes
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.http.content.defaultResource
import io.ktor.server.http.content.resources
import io.ktor.server.http.content.static
import org.climatechangemakers.act.di.ApiComponent
import org.climatechangemakers.act.feature.bill.routing.billRoutes
import org.climatechangemakers.act.feature.cms.routing.contentManagementRoutes
import org.climatechangemakers.act.feature.values.routing.valuesRoutes

fun Application.configureRouting(apiComponent: ApiComponent) {

  routing {
    route("/api") {
      actionRoutes(apiComponent.actionController())
      issueRoutes(apiComponent.issueController())
      valuesRoutes(apiComponent.valuesController())
      billRoutes(apiComponent.billController())
    }

    contentManagementRoutes()

    static {
      resources("client")
      defaultResource("client/index.html")
    }
  }
}
