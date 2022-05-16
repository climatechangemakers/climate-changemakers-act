package org.climatechangemakers.act.feature.communicatewithcongress.manager

import io.ktor.util.error
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import okio.ByteString
import okio.ByteString.Companion.encodeUtf8
import org.climatechangemakers.act.common.model.Result
import org.climatechangemakers.act.common.model.Success
import org.climatechangemakers.act.common.model.Failure
import org.climatechangemakers.act.common.serializers.toAlphaNumericString
import org.climatechangemakers.act.feature.action.manager.ActionTrackerManager
import org.climatechangemakers.act.feature.action.model.SendEmailRequest
import org.climatechangemakers.act.feature.communicatewithcongress.model.CommunicateWithCogressRequest
import org.climatechangemakers.act.feature.communicatewithcongress.model.Constituent
import org.climatechangemakers.act.feature.communicatewithcongress.model.Delivery
import org.climatechangemakers.act.feature.communicatewithcongress.model.Message
import org.climatechangemakers.act.feature.communicatewithcongress.model.Recipient
import org.climatechangemakers.act.feature.communicatewithcongress.service.HouseCommunicateWithCongressService
import org.climatechangemakers.act.feature.communicatewithcongress.service.SenateCommunicateWithCongressService
import org.climatechangemakers.act.feature.findlegislator.manager.MemberOfCongressManager
import org.climatechangemakers.act.feature.findlegislator.model.LegislatorRole
import org.climatechangemakers.act.feature.findlegislator.model.MemberOfCongress
import org.climatechangemakers.act.feature.issue.manager.IssueManager
import org.slf4j.Logger
import retrofit2.HttpException
import javax.inject.Inject

class NetworkCommunicateWithCongressManager @Inject constructor(
  private val senateService: SenateCommunicateWithCongressService,
  private val houseService: HouseCommunicateWithCongressService,
  private val memberOfCongressManager: MemberOfCongressManager,
  private val actionTrackerManager: ActionTrackerManager,
  private val issueManager: IssueManager,
  private val logger: Logger,
) : CommunicateWithCongressManager {

  override suspend fun sendEmails(request: SendEmailRequest): Result<String, List<String>> {
    val failedIds = coroutineScope {
      request.contactedBioguideIds.map { bioguideId ->
        async {
          if (sendEmailToMemberOfCongress(bioguideId, request)) null else bioguideId
        }
      }.awaitAll().filterNotNull()
    }

    return if (failedIds.isEmpty()) Success(request.emailBody) else Failure(failedIds)
  }

  private suspend fun sendEmailToMemberOfCongress(bioguideId: String, request: SendEmailRequest): Boolean {
    val memberOfCongress = memberOfCongressManager.getMemberOfCongressForBioguide(bioguideId)
    return if (memberOfCongress.cwcOfficeCode != null) {
      try {
        val deliveryId = sendEmailViaCWC(memberOfCongress, request)
        actionTrackerManager.trackActionSendEmail(
          email = request.originatingEmailAddress,
          contactedBioguideId = bioguideId,
          relatedIssueId = request.relatedIssueId,
          emailDeliveryId = deliveryId,
        )
        true
      } catch (e: HttpException) {
        e.response()?.errorBody()?.string()?.let(logger::error)
        false
      } catch (e: Exception) {
        logger.error(e)
        false
      }
    } else {
      true
    }
  }

  private suspend fun sendEmailViaCWC(
    memberOfConress: MemberOfCongress,
    emailRequest: SendEmailRequest,
  ): String {
    val cwcRequest = CommunicateWithCogressRequest(
      delivery = Delivery(campaignId = getCampaignIdForIssue(emailRequest.relatedIssueId)),
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
      LegislatorRole.Senator -> senateService.contact(cwcRequest)
      LegislatorRole.Representative -> houseService.contact(cwcRequest)
    }

    return cwcRequest.delivery.deliveryId.toAlphaNumericString()
  }

  private suspend fun getCampaignIdForIssue(issueId: Long): String {
    // TODO(kcianfarini) This value should live in the DB, but currently SQLDelight doesn't support the type BYTEA
    val issueTitle = issueManager.getIssueTitleForId(issueId)
    return issueTitle.encodeUtf8().sha256().hex()
  }
}