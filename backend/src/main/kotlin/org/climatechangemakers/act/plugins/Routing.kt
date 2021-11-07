package org.climatechangemakers.act.plugins

import org.climatechangemakers.act.feature.action.routing.actionRoutes
import org.climatechangemakers.act.feature.issue.routing.issueRoutes
import io.ktor.routing.*
import io.ktor.application.*
import io.ktor.http.content.defaultResource
import io.ktor.http.content.resources
import io.ktor.http.content.static
import org.climatechangemakers.act.di.ApiComponent
import org.climatechangemakers.act.feature.membership.routing.membershipRoutes
import org.climatechangemakers.act.feature.values.routing.valuesRoutes

fun Application.configureRouting(apiComponent: ApiComponent) {

  routing {
    route("/api") {
      actionRoutes(apiComponent.actionController())
      issueRoutes(apiComponent.issueController())
      membershipRoutes(apiComponent.membershipController())
      valuesRoutes(apiComponent.valuesController())
    }

    static {
      resources("client")
      defaultResource("client/index.html")
    }
  }
}
