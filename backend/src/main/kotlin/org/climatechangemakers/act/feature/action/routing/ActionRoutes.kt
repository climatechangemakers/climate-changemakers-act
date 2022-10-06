package org.climatechangemakers.act.feature.action.routing

import org.climatechangemakers.act.feature.action.controller.ActionController
import io.ktor.server.application.call
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

fun Route.actionRoutes(controller: ActionController) {

  post("/initiate-action") { controller.initiateAction(call) }
  post("/send-email") { controller.sendEmailToLegislators(call) }
  post("/log-call") { controller.logLegislatorCallAction(call) }
  post("/log-tweet") { controller.logLegislatorTweetAction(call) }
}