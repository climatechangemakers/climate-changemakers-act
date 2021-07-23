package com.climatechangemakers.act.feature.action.routing

import com.climatechangemakers.act.feature.action.controller.ActionController
import io.ktor.application.call
import io.ktor.routing.Routing
import io.ktor.routing.post

fun Routing.actionRoutes(controller: ActionController) {

  post("/initiate-action") {
    controller.initiateAction(call)
  }
}