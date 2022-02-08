package org.climatechangemakers.act.feature.email.service

import kotlinx.coroutines.channels.Channel
import org.climatechangemakers.act.feature.email.model.EnrollMemberRequest
import org.climatechangemakers.act.feature.email.model.SubscribeNewsletterRequest
import retrofit2.Response

class FakeMailchimpService(private val responseGenerator: () -> Response<Void>) : MailchimpService {

  val capturedAudienceIds = Channel<String>(Channel.UNLIMITED)
  val capturedEnrollmentRequestBodies = Channel<EnrollMemberRequest>(Channel.UNLIMITED)
  val capturedSubscribeRequestBodies = Channel<SubscribeNewsletterRequest>(Channel.UNLIMITED)
  val capturedEmailHashes = Channel<String>(Channel.UNLIMITED)

  override suspend fun subscribeChangemaker(
    audienceId: String,
    emailMd5Hash: String,
    request: EnrollMemberRequest,
  ) {
    capturedAudienceIds.trySend(audienceId)
    capturedEnrollmentRequestBodies.trySend(request)
    capturedEmailHashes.trySend(emailMd5Hash)
  }

  override suspend fun subscribeChangemaker(
    audienceId: String,
    emailMd5Hash: String,
    request: SubscribeNewsletterRequest
  ) {
    capturedAudienceIds.trySend(audienceId)
    capturedSubscribeRequestBodies.trySend(request)
    capturedEmailHashes.trySend(emailMd5Hash)
  }

  override suspend fun checkSubscription(audienceId: String, emailMd5Hash: String): Response<Void> {
    capturedAudienceIds.trySend(audienceId)
    capturedEmailHashes.trySend(emailMd5Hash)
    return responseGenerator()
  }
}