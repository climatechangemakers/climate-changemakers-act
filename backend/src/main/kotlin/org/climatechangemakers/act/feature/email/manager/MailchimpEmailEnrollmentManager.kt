package org.climatechangemakers.act.feature.email.manager

import okio.ByteString
import org.climatechangemakers.act.common.model.RepresentedArea
import org.climatechangemakers.act.di.Mailchimp
import org.climatechangemakers.act.feature.email.model.SubscribeChangemakerRequest
import org.climatechangemakers.act.feature.email.service.MailchimpService
import javax.inject.Inject

class MailchimpEmailEnrollmentManager @Inject constructor(
  private val mailchimpService: MailchimpService,
  @Mailchimp private val changemakersMailchimpAudienceId: String,
) : EmailEnrollmentManager {

  override suspend fun subscribeChangemaker(
    email: String,
    firstName: String,
    lastName: String,
    state: RepresentedArea,
  ) {
    val request = SubscribeChangemakerRequest(email, firstName, lastName, state)
    mailchimpService.subscribeChangemaker(
      audienceId = changemakersMailchimpAudienceId,
      emailMd5Hash = ByteString.of(*email.lowercase().encodeToByteArray()).md5().hex(),
      request = request,
    )
  }
}