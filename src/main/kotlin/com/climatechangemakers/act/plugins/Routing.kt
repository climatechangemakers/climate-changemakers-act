package com.climatechangemakers.act.plugins

import com.climatechangemakers.act.di.DaggerApiComponent
import com.climatechangemakers.act.feature.representativefinder.routing.legislatorRoutes
import io.ktor.routing.*
import io.ktor.application.*
import io.ktor.response.*

fun Application.configureRouting() {
  // Starting point for a Ktor app:

  val apiComponent = DaggerApiComponent.create()

  routing {
    get("/") {
      call.respondText("Hello World!")
    }

    legislatorRoutes(apiComponent.representativeFinderManager())
  }
}
