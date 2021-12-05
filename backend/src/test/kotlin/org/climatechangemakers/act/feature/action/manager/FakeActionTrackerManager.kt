package org.climatechangemakers.act.feature.action.manager

import kotlinx.coroutines.channels.Channel

class FakeActionTrackerManager : ActionTrackerManager {

  val capturedEmail = Channel<String>(capacity = Channel.BUFFERED)
  val capturedBioguideId = Channel<String>(capacity = Channel.BUFFERED)
  val capturedIssueId = Channel<Long>(capacity = Channel.BUFFERED)

  override suspend fun trackActionInitiated(email: String): Unit = TODO("Not yet implemented")
  override suspend fun trackActionSendEmail(email: String, contactedBioguideId: String, relatedIssueId: Long) {
    capturedEmail.trySend(email)
    capturedBioguideId.trySend(contactedBioguideId)
    capturedIssueId.trySend(relatedIssueId)
  }

  override suspend fun trackActionPhoneCall(
    email: String,
    contactedBioguideId: String,
    relatedIssueId: Long,
  ) = TODO("Not yet implemented")

  override suspend fun trackTweet(email: String, contactedBioguideIds: List<String>, relatedIssueId: Long) {
    TODO("Not yet implemented")
  }

  override suspend fun trackActionSignUp(email: String) {
    capturedEmail.trySend(email)
  }
}