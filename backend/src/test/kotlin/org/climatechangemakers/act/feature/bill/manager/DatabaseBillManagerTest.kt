package org.climatechangemakers.act.feature.bill.manager

import org.climatechangemakers.act.common.extension.state
import org.climatechangemakers.act.database.Database
import org.climatechangemakers.act.feature.bill.model.Bill
import org.climatechangemakers.act.feature.bill.model.BillType
import org.climatechangemakers.act.feature.findlegislator.util.suspendTest
import org.climatechangemakers.act.feature.util.TestContainerProvider
import org.postgresql.util.PSQLException
import org.postgresql.util.PSQLState
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DatabaseBillManagerTest : TestContainerProvider() {

  @Test fun `manager saves a bill`() = suspendTest {
    val bill = Bill(
      congressionalSession = 117,
      type = BillType.HouseBill,
      number = 1,
      name = "first bill!",
    )
    manager().persistBill(bill)
    assertEquals(
      expected = 1L,
      actual = selectCountBills(),
    )
  }

  @Test fun `manager throws on unique constraint violation`() = suspendTest {
    val bill = Bill(
      congressionalSession = 117,
      type = BillType.HouseBill,
      number = 1,
      name = "first bill!",
    )
    val manager = manager()
    manager.persistBill(bill)
    val e = assertFailsWith<PSQLException> {
      manager.persistBill(bill)
    }

    assertEquals(
      expected = PSQLState.UNIQUE_VIOLATION,
      actual = e.state,
    )
  }

  private fun selectCountBills(): Long = driver.executeQuery(
    identifier = null,
    sql = "SELECT COUNT(*) FROM congress_bill;",
    mapper = { cursor -> cursor.also { it.next() }.getLong(0)!! },
    parameters = 0,
  ).value

  private fun manager(
    database: Database = this.database,
    coroutineContext: CoroutineContext = EmptyCoroutineContext,
  ) = DatabaseBillManager(database, coroutineContext)
}