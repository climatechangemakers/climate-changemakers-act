package org.climatechangemakers.act.feature.bill.manager

import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import org.climatechangemakers.act.common.extension.congressionalSession
import org.climatechangemakers.act.database.Database
import org.climatechangemakers.act.di.Io
import org.climatechangemakers.act.feature.bill.model.Bill
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class DatabaseBillManager @Inject constructor(
  private val clock: Clock,
  database: Database,
  @Io private val ioContext: CoroutineContext,
): BillManager {

  private val billQueries = database.congressBillAndIssueQueries

  override suspend fun getBillsForIssueId(
    issueId: Long
  ): List<Bill> = withContext(ioContext) {
    billQueries.selectBillsForIssueAndCongressionalSession(
      issueId = issueId,
      session = clock.now().congressionalSession,
      mapper = ::Bill,
    ).executeAsList()
  }
}