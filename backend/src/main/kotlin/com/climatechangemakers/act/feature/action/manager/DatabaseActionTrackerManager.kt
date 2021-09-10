package com.climatechangemakers.act.feature.action.manager

import com.climatechangemakers.act.database.Database
import com.climatechangemakers.act.di.Io
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class DatabaseActionTrackerManager @Inject constructor(
  database: Database,
  @Io private val ioDispatcher: CoroutineContext,
) : ActionTrackerManager {

  private val initiateActionQueries = database.actionInitiateQueries
  private val actionEmailLegislatorQueries = database.actionEmailLegislatorQueries

  override suspend fun trackActionInitiated(email: String) = withContext(ioDispatcher) {
    initiateActionQueries.insert(email)
  }

  override suspend fun trackActionSendEmails(
    email: String,
    contactedBioguideIds: List<String>,
    relatedIssueId: Long,
  ) = withContext(ioDispatcher) {
    contactedBioguideIds.forEach { bioguide ->
      launch { actionEmailLegislatorQueries.insert(email, relatedIssueId, bioguide) }
    }
  }
}