package org.climatechangemakers.act.plugins

import org.climatechangemakers.act.di.DaggerApiComponent
import org.climatechangemakers.act.feature.action.routing.actionRoutes
import org.climatechangemakers.act.feature.issue.routing.issueRoutes
import io.ktor.routing.*
import io.ktor.application.*
import io.ktor.response.*

fun Application.configureRouting() {
  val apiComponent = org.climatechangemakers.act.di.DaggerApiComponent.create()

  routing {
    route("/api") {
      actionRoutes(apiComponent.actionController())
      issueRoutes(apiComponent.issueController())
    }
  }
}