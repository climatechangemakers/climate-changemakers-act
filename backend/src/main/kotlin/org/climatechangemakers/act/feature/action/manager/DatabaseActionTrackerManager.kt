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
    contactedPhoneNumber: String
  ) = withContext(ioDispatcher) {
    // TODO(kcianfarini) get rid of this when transaction RETURNING is supported.
    // TODO(kcianfarini) related issue: https://github.com/AlecStrong/sql-psi/issues/173
    actionCallLegislatorQueries.transaction {
      actionContactLegislatorQueries.insert(email, relatedIssueId, contactedBioguideId)
      val insertedId: Long = actionContactLegislatorQueries.getMaxId().executeAsOne()
      actionCallLegislatorQueries.insert(insertedId, contactedPhoneNumber)
    }
  }
}