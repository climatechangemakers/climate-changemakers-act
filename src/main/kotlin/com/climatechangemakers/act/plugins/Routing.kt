package com.climatechangemakers.act.plugins

import com.climatechangemakers.act.di.DaggerApiComponent
import com.climatechangemakers.act.feature.action.routing.actionRoutes
import io.ktor.routing.*
import io.ktor.application.*
import io.ktor.response.*

fun Application.configureRouting() {
  val apiComponent = DaggerApiComponent.create()

  routing {
    get("/") {
      call.respondText("Hello World!")
    }

    actionRoutes(apiComponent.actionController())
  }
}
