package org.climatechangemakers.act.feature.action.controller

import org.climatechangemakers.act.feature.action.manager.ActionTrackerManager
import org.climatechangemakers.act.feature.action.model.InitiateActionRequest
import org.climatechangemakers.act.feature.action.model.InitiateActionResponse
import org.climatechangemakers.act.feature.action.model.LogPhoneCallRequest
import org.climatechangemakers.act.feature.action.model.SendEmailRequest
import org.climatechangemakers.act.feature.findlegislator.manager.LegislatorsManager
import org.climatechangemakers.act.feature.findlegislator.model.GetLegislatorsByAddressRequest
import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.climatechangemakers.act.common.extension.respondNothing
import org.climatechangemakers.act.common.model.Failure
import org.climatechangemakers.act.common.model.Success
import org.climatechangemakers.act.feature.action.model.LogTweetRequest
import org.climatechangemakers.act.feature.action.model.SendEmailErrorResponse
import org.climatechangemakers.act.feature.action.model.SendEmailResponse
import org.climatechangemakers.act.feature.action.model.SignUpRequest
import org.climatechangemakers.act.feature.communicatewithcongress.manager.CommunicateWithCongressManager
import javax.inject.Inject

class ActionController @Inject constructor(
  private val legislatorsManager: LegislatorsManager,
  private val actionTrackerManager: ActionTrackerManager,
  private val communicateWithCongressManager: CommunicateWithCongressManager,
) {

  suspend fun initiateAction(call: ApplicationCall) {
    val request = call.receive<InitiateActionRequest>()

    val response = coroutineScope {
      launch { actionTrackerManager.trackActionInitiated(request.email) }
      async {
        InitiateActionResponse(request.email, legislatorsManager.getLegislators(request.toGetLegislatorsRequest()))
      }
    }

    call.respond(response.await())
  }

  suspend fun sendEmailToLegislators(call: ApplicationCall) {
    val request = call.receive<SendEmailRequest>()
    when (val result = communicateWithCongressManager.sendEmails(request)) {
      is Success -> call.respond(HttpStatusCode.OK, SendEmailResponse(result.data))
      is Failure -> call.respond(HttpStatusCode.InternalServerError, SendEmailErrorResponse(result.errorData))
    }
  }

  suspend fun logLegislatorCallAction(call: ApplicationCall) {
    val request = call.receive<LogPhoneCallRequest>()

    actionTrackerManager.trackActionPhoneCall(
      request.originatingEmailAddress,
      request.contactedBioguideId,
      request.relatedIssueId,
      request.contactedPhoneNumber,
    )

    call.respondNothing()
  }

  suspend fun logLegislatorTweetAction(call: ApplicationCall) {
    val request = call.receive<LogTweetRequest>()

    actionTrackerManager.trackTweet(
      email = request.originatingEmailAddress,
      contactedBioguideIds = request.contactedBioguideIds,
      relatedIssueId = request.relatedIssueId,
    )

    call.respondNothing()
  }

  suspend fun signUp(call: ApplicationCall) {
    // TODO(kcianfarini) implement
    call.receive<SignUpRequest>()
    call.respond(HttpStatusCode.Created)
  }
}

private fun InitiateActionRequest.toGetLegislatorsRequest() = GetLegislatorsByAddressRequest(
  streetAddress = streetAddress,
  city = city,
  state = state,
  postalCode = postalCode,
)