package org.climatechangemakers.act.feature.membership.controller

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import org.climatechangemakers.act.feature.membership.model.CheckMembershipRequest
import org.climatechangemakers.act.feature.membership.model.CheckMembershipResponse
import javax.inject.Inject

class MembershipController @Inject constructor() {

  suspend fun checkMembership(call: ApplicationCall) {
    call.receive<CheckMembershipRequest>()
    call.respond(HttpStatusCode.OK, CheckMembershipResponse(false))
  }
}