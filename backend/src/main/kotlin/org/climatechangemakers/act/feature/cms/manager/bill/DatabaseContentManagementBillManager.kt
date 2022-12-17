package org.climatechangemakers.act.feature.cms.manager.bill

import kotlinx.coroutines.withContext
import org.climatechangemakers.act.common.extension.executeAsOneOrNotFound
import org.climatechangemakers.act.database.Database
import org.climatechangemakers.act.di.Io
import org.climatechangemakers.act.feature.bill.model.Bill
import org.climatechangemakers.act.feature.cms.model.bill.CreateBill
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class DatabaseContentManagementBillManager @Inject constructor(
  database: Database,
  @Io private val coroutineContext: CoroutineContext,
) : ContentManagementBillManager {

  private val billQueries = database.congressBillQueries

  override suspend fun persistBill(bill: CreateBill) = withContext(coroutineContext) {
    billQueries.insert(
      congressionalSession = bill.congressionalSession,
      billType = bill.type,
      billNumber = bill.number,
      billName = bill.name,
      url = bill.url,
      mapper = ::Bill,
    ).executeAsOne()
  }


  override suspend fun updateBill(bill: Bill) = withContext(coroutineContext) {
    billQueries.update(
      id = bill.id,
      congressionalSession = bill.congressionalSession,
      billType = bill.type,
      billNumber = bill.number,
      billName = bill.name,
      url = bill.url,
      mapper = ::Bill,
    ).executeAsOneOrNotFound("Bill with id ${bill.id} does not exist.")
  }

  override suspend fun getBills(): List<Bill> = withContext(coroutineContext) {
    billQueries.selectAll(::Bill).executeAsList()
  }
}