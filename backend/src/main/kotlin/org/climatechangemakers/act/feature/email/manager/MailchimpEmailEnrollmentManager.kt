package org.climatechangemakers.act.feature.email.manager

import okio.ByteString
import org.climatechangemakers.act.common.model.RepresentedArea
import org.climatechangemakers.act.di.Mailchimp
import org.climatechangemakers.act.feature.email.model.EnrollMemberRequest
import org.climatechangemakers.act.feature.email.model.SubscribeNewsletterRequest
import org.climatechangemakers.act.feature.email.service.MailchimpService
import retrofit2.HttpException
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
    val request = EnrollMemberRequest(email, firstName, lastName, state)
    mailchimpService.subscribeChangemaker(
      audienceId = changemakersMailchimpAudienceId,
      emailMd5Hash = email.md5(),
      request = request,
    )
  }

  override suspend fun subscribeChangemaker(email: String) {
    if (!checkSubscription(email)) {
      mailchimpService.subscribeChangemaker(
        audienceId = changemakersMailchimpAudienceId,
        emailMd5Hash = email.md5(),
        request = SubscribeNewsletterRequest(email)
      )
    }
  }

  private suspend fun checkSubscription(email: String): Boolean {
    val response = mailchimpService.checkSubscription(
      audienceId = changemakersMailchimpAudienceId,
      emailMd5Hash = email.md5(),
    )

    return when {
      response.isSuccessful -> true
      response.code() == 404 -> false
      else -> throw HttpException(response)
    }
  }
}

private fun String.md5() = ByteString.of(*lowercase().encodeToByteArray()).md5().hex()