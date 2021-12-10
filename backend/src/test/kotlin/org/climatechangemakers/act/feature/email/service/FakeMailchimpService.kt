package org.climatechangemakers.act.feature.email.service

import kotlinx.coroutines.channels.Channel
import org.climatechangemakers.act.feature.email.model.SubscribeChangemakerRequest

class FakeMailchimpService : MailchimpService {

  val capturedAudienceIds = Channel<String>(Channel.UNLIMITED)
  val capturedRequestBodies = Channel<SubscribeChangemakerRequest>(Channel.UNLIMITED)

  override suspend fun subscribeChangemaker(audienceId: String, request: SubscribeChangemakerRequest) {
    capturedAudienceIds.trySend(audienceId)
    capturedRequestBodies.trySend(request)
  }
}