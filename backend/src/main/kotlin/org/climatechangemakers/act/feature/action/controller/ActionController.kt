package org.climatechangemakers.act.feature.action.controller

import org.climatechangemakers.act.feature.action.manager.ActionTrackerManager
import org.climatechangemakers.act.feature.action.model.InitiateActionRequest
import org.climatechangemakers.act.feature.action.model.InitiateActionResponse
import org.climatechangemakers.act.feature.action.model.LogPhoneCallRequest
import org.climatechangemakers.act.feature.action.model.SendEmailRequest
import org.climatechangemakers.act.feature.findlegislator.manager.LegislatorsManager
import org.climatechangemakers.act.feature.findlegislator.model.GetLegislatorsByAddressRequest
import io.ktor.application.ApplicationCall
import io.ktor.request.receive
import io.ktor.response.respond
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.climatechangemakers.act.common.extension.respondNothing
import org.climatechangemakers.act.feature.action.model.LogTweetRequest
import javax.inject.Inject

class ActionController @Inject constructor(
  private val legislatorsManager: LegislatorsManager,
  private val actionTrackerManager: ActionTrackerManager,
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

    coroutineScope {
      launch {
        actionTrackerManager.trackActionSendEmails(
          request.originatingEmailAddress,
          request.contactedBioguideIds,
          request.relatedIssueId,
        )
      }
    }

    call.respondNothing()
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
}

private fun InitiateActionRequest.toGetLegislatorsRequest() = GetLegislatorsByAddressRequest(
  streetAddress = streetAddress,
  city = city,
  state = state,
  postalCode = postalCode,
)