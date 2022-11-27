package org.climatechangemakers.act.feature.cms.manager.issue

import kotlinx.coroutines.withContext
import org.climatechangemakers.act.common.util.exists
import org.climatechangemakers.act.database.Database
import org.climatechangemakers.act.di.Io
import org.climatechangemakers.act.feature.bill.model.Bill
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class DatabaseIssueAndBillAssociationManager @Inject constructor(
  database: Database,
  @Io private val coroutineContext: CoroutineContext,
) : IssueAndBillAssociationManager {

  private val issueAndFocusQueries = database.issueAndFocusQueries
  private val billAndIssueQueries = database.congressBillAndIssueQueries

  override suspend fun getBillsForIssueId(
    issueId: Long
  ): List<Bill> = withContext(coroutineContext) {
    exists(issueAndFocusQueries.rowCount(issueId).executeAsOne() == 1L) {
      "Issue with id $issueId does not exist!"
    }
    billAndIssueQueries.selectBillsForIssueId(issueId, ::Bill).executeAsList()
  }

  override suspend fun associateBillsToIssue(
    issueId: Long,
    billIds: List<Long>,
  ) = withContext(coroutineContext) {
    billAndIssueQueries.transaction {
      billAndIssueQueries.deleteForIssueId(issueId)
      billIds.forEach { billId ->
        billAndIssueQueries.insert(issueId, billId)
      }
    }
  }
}