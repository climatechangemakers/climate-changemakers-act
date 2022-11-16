package org.climatechangemakers.act.feature.cms.manager.bill

import kotlinx.coroutines.withContext
import org.climatechangemakers.act.database.Database
import org.climatechangemakers.act.di.Io
import org.climatechangemakers.act.feature.bill.model.Bill
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class DatabaseContentManagementBillManager @Inject constructor(
  database: Database,
  @Io private val coroutineContext: CoroutineContext,
) : ContentManagementBillManager {

  private val billQueries = database.congressBillQueries

  override suspend fun persistBill(bill: Bill) = withContext(coroutineContext) {
    billQueries.insert(
      congressionalSession = bill.congressionalSession,
      billType = bill.type,
      billNumber = bill.number,
      billName = bill.name,
      url = bill.url,
    )
  }
}