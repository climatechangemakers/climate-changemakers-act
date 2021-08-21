package com.climatechangemakers.act.plugins

import com.climatechangemakers.act.di.DaggerApiComponent
import com.climatechangemakers.act.feature.action.routing.actionRoutes
import com.climatechangemakers.act.feature.issue.routing.issueRoutes
import io.ktor.routing.*
import io.ktor.application.*
import io.ktor.response.*

fun Application.configureRouting() {
  val apiComponent = DaggerApiComponent.create()

  routing {
    route("/api") {
      actionRoutes(apiComponent.actionController())
      issueRoutes(apiComponent.issueController())
    }
  }
}
