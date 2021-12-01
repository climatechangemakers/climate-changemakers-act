package org.climatechangemakers.act.feature.membership.routing

import io.ktor.application.call
import io.ktor.routing.Route
import io.ktor.routing.post
import org.climatechangemakers.act.feature.membership.controller.MembershipController

fun Route.membershipRoutes(controller: MembershipController) {

  post("/check-membership") { controller.checkMembership(call) }
  post("/sign-up") { controller.signUp(call) }
}