package com.climatechangemakers.act.feature.action.routing

import com.climatechangemakers.act.feature.action.controller.ActionController
import io.ktor.application.call
import io.ktor.routing.Route
import io.ktor.routing.post

fun Route.actionRoutes(controller: ActionController) {

  post("/initiate-action") { controller.initiateAction(call) }
  post("/send-email") { controller.sendEmailToLegislators(call) }
  post("/log-call") { controller.logLegislatorCallAction(call) }
}