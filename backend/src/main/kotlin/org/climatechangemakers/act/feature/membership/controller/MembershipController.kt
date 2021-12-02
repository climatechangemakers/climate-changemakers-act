package org.climatechangemakers.act.feature.membership.controller

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import org.climatechangemakers.act.feature.membership.manager.MembershipManager
import org.climatechangemakers.act.feature.membership.model.CheckMembershipRequest
import org.climatechangemakers.act.feature.membership.model.CheckMembershipResponse
import org.climatechangemakers.act.feature.membership.model.SignUpRequest
import javax.inject.Inject

class MembershipController @Inject constructor(
  private val manager: MembershipManager,
) {

  suspend fun checkMembership(call: ApplicationCall) {
    val request = call.receive<CheckMembershipRequest>()
    call.respond(
      HttpStatusCode.OK,
      CheckMembershipResponse(manager.checkMembership(request.email)),
    )
  }

  suspend fun signUp(call: ApplicationCall) {
    val request = call.receive<SignUpRequest>()
    manager.signUp(
      request.email,
      request.firstName,
      request.lastName,
      request.city,
      request.state,
      request.priorExperience,
      request.referral,
      request.actionReason,
      request.socialVerification,
    )
    call.respond(HttpStatusCode.Created)
  }
}