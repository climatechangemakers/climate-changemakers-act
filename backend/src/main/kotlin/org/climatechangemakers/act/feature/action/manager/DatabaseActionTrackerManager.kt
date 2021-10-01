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
  private val actionContactLegislatorQueries = database.actionContactLegislatorQueries
  private val actionCallLegislatorQueries = database.actionCallLegislatorQueries
  private val actionTweetLegislatorQueries = database.actionTweetLegislatorQueries

  override suspend fun trackActionInitiated(email: String) = withContext(ioDispatcher) {
    initiateActionQueries.insert(email)
  }

  override suspend fun trackActionSendEmails(
    email: String,
    contactedBioguideIds: List<String>,
    relatedIssueId: Long,
  ) = withContext(ioDispatcher) {
    contactedBioguideIds.forEach { bioguide ->
      launch { actionContactLegislatorQueries.insert(email, relatedIssueId, bioguide) }
    }
  }

  override suspend fun trackActionPhoneCall(
    email: String,
    contactedBioguideId: String,
    relatedIssueId: Long,
    contactedPhoneNumber: String,
  ) = withContext(ioDispatcher) {
    actionCallLegislatorQueries.insert(email, relatedIssueId, contactedBioguideId, contactedPhoneNumber)
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
}