package org.climatechangemakers.act.feature.action.manager

import org.climatechangemakers.act.database.Database
import org.climatechangemakers.act.di.Io
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class DatabaseActionTrackerManager @Inject constructor(
  database: Database,
  @Io private val ioDispatcher: CoroutineContext,
) : ActionTrackerManager {

  private val initiateActionQueries = database.actionInitiateQueries
  private val actionCallLegislatorQueries = database.actionCallLegislatorQueries
  private val actionTweetLegislatorQueries = database.actionTweetLegislatorQueries
  private val actionEmailLegislatorQueries = database.actionEmailLegislatorQueries
  private val actionSignUpQueries = database.actionSignUpQueries

  override suspend fun trackActionInitiated(email: String) = withContext(ioDispatcher) {
    initiateActionQueries.insert(email)
  }

  override suspend fun trackActionSendEmail(
    email: String,
    contactedBioguideId: String,
    relatedIssueId: Long,
    emailDeliveryId: String,
  ) = withContext(ioDispatcher) {
    actionEmailLegislatorQueries.insert(email, relatedIssueId, contactedBioguideId, emailDeliveryId)
  }

  override suspend fun trackActionPhoneCall(
    email: String,
    contactedBioguideId: String,
    relatedIssueId: Long,
  ) = withContext(ioDispatcher) {
    actionCallLegislatorQueries.insert(email, relatedIssueId, contactedBioguideId)
  }

  override suspend fun trackTweet(
    email: String,
    contactedBioguideIds: List<String>,
    relatedIssueId: Long,
  ) = withContext(ioDispatcher) {
    contactedBioguideIds.forEach { bioguideId ->
      launch { actionTweetLegislatorQueries.insert(email, relatedIssueId, bioguideId) }
    }
  }

  override suspend fun trackActionSignUp(email: String) = withContext(ioDispatcher) {
    actionSignUpQueries.insert(email)
  }
}