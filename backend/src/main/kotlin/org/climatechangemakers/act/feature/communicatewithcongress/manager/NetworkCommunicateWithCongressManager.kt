package org.climatechangemakers.act.feature.communicatewithcongress.manager

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.climatechangemakers.act.di.Senate
import org.climatechangemakers.act.feature.action.manager.ActionTrackerManager
import org.climatechangemakers.act.feature.action.model.SendEmailRequest
import org.climatechangemakers.act.feature.communicatewithcongress.model.CommunicateWithCogressRequest
import org.climatechangemakers.act.feature.communicatewithcongress.model.Constituent
import org.climatechangemakers.act.feature.communicatewithcongress.model.Delivery
import org.climatechangemakers.act.feature.communicatewithcongress.model.Message
import org.climatechangemakers.act.feature.communicatewithcongress.model.Recipient
import org.climatechangemakers.act.feature.communicatewithcongress.service.CommunicateWithCongressService
import org.climatechangemakers.act.feature.findlegislator.manager.MemberOfCongressManager
import org.climatechangemakers.act.feature.findlegislator.model.LegislatorRole
import org.climatechangemakers.act.feature.findlegislator.model.MemberOfCongress
import javax.inject.Inject

class NetworkCommunicateWithCongressManager @Inject constructor(
  @Senate private val service: CommunicateWithCongressService,
  private val memberOfCongressManager: MemberOfCongressManager,
  private val actionTrackerManager: ActionTrackerManager,
) : CommunicateWithCongressManager {

  override suspend fun sendEmails(request: SendEmailRequest) = coroutineScope {
    request.contactedBioguideIds.forEach { bioguideId ->
      launch {
        sendEmailToMemberOfCongress(bioguideId, request)
        actionTrackerManager.trackActionSendEmail(
          request.originatingEmailAddress,
          bioguideId,
          request.relatedIssueId,
        )
      }
    }
  }

  private suspend fun sendEmailToMemberOfCongress(bioguideId: String, request: SendEmailRequest) {
    val memberOfCongress = memberOfCongressManager.getMemberOfCongressForBioguide(bioguideId)
    if (memberOfCongress.cwcOfficeCode != null) {
      sendEmailViaCWC(memberOfCongress, request)
    }
  }

  private suspend fun sendEmailViaCWC(memberOfConress: MemberOfCongress, emailRequest: SendEmailRequest) {
    val cwcRequest = CommunicateWithCogressRequest(
      delivery = Delivery(campaignId = "campaign Id"), // TODO(kcianfarini)
      recipient = Recipient(officeCode = memberOfConress.cwcOfficeCode!!),
      constituent = Constituent(
        prefix = emailRequest.title,
        firstName = emailRequest.firstName,
        lastName = emailRequest.lastName,
        address = emailRequest.streetAddress,
        city = emailRequest.city,
        state = emailRequest.state,
        postalCode = emailRequest.postalCode,
        email = emailRequest.originatingEmailAddress,
      ),
      message = Message(
        subject = emailRequest.emailSubject,
        topics = emailRequest.relatedTopics,
        bills = emptyList(), // TODO(kcianfarini) Github issue #95
        body = emailRequest.emailBody,
      ),
    )

    when (memberOfConress.legislativeRole) {
      LegislatorRole.Senator -> service.contact(cwcRequest)
      LegislatorRole.Representative -> Unit
    }
  }
}