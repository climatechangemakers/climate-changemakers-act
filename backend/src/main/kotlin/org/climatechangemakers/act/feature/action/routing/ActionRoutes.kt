package org.climatechangemakers.act.feature.action.routing

import org.climatechangemakers.act.feature.action.controller.ActionController
import io.ktor.application.call
import io.ktor.routing.Route
import io.ktor.routing.post

fun Route.actionRoutes(controller: ActionController) {

  post("/initiate-action") { controller.initiateAction(call) }
  post("/send-email") { controller.sendEmailToLegislators(call) }
  post("/log-call") { controller.logLegislatorCallAction(call) }
  post("/log-tweet") { controller.logLegislatorTweetAction(call) }
  post("/sign-up") { controller.signUp(call) }
}