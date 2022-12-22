package org.climatechangemakers.act.feature.cms.manager.bill

import org.climatechangemakers.act.common.extension.state
import org.climatechangemakers.act.database.Database
import org.climatechangemakers.act.feature.bill.model.Bill
import org.climatechangemakers.act.feature.bill.model.BillType
import org.climatechangemakers.act.feature.cms.model.bill.CreateBill
import org.climatechangemakers.act.feature.findlegislator.util.suspendTest
import org.climatechangemakers.act.feature.util.TestContainerProvider
import org.postgresql.util.PSQLException
import org.postgresql.util.PSQLState
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DatabaseContentManagementBillManagerTest : TestContainerProvider() {

  @Test fun `manager saves a bill`() = suspendTest {
    val bill = CreateBill(
      congressionalSession = 117,
      type = BillType.HouseBill,
      number = 1,
      name = "first bill!",
      url = "some.url",
    )
    manager().persistBill(bill)
    assertEquals(
      expected = 1L,
      actual = selectCountBills(),
    )
  }

  @Test fun `manager throws on unique constraint violation`() = suspendTest {
    val bill = CreateBill(
      congressionalSession = 117,
      type = BillType.HouseBill,
      number = 1,
      name = "first bill!",
      url = "some.url",
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

  @Test fun `manager updates bill`() = suspendTest {
    val createBill = CreateBill(
      congressionalSession = 117,
      type = BillType.HouseBill,
      number = 1,
      name = "first bill!",
      url = "some.url",
    )
    val sut = manager()
    sut.persistBill(createBill)
    val bill = sut.getBills().first()
    val updated = bill.copy(congressionalSession = 1)
    assertEquals(
      expected = updated,
      actual = sut.updateBill(updated),
    )
  }

  @Test fun `manager throws NSE trying to update bill nonexistant bill`() = suspendTest {
    val bill = Bill(
      id = -1,
      congressionalSession = 117,
      type = BillType.HouseBill,
      number = 1,
      name = "first bill!",
      url = "some.url",
    )

    assertFailsWith<NoSuchElementException> { manager().updateBill(bill) }
  }

  @Test fun `manager returns list of bills`() = suspendTest {
    val bill = CreateBill(
      congressionalSession = 117,
      type = BillType.HouseBill,
      number = 1,
      name = "first bill!",
      url = "some.url",
    )

    val sut = manager()
    sut.persistBill(bill)

    assertEquals(
      expected = 1,
      actual = sut.getBills().size,
    )
  }

  @Test fun `manager deletes bill`() = suspendTest {
    val sut = manager()
    val bill = sut.persistBill(
      CreateBill(
        congressionalSession = 117,
        type = BillType.HouseBill,
        number = 1,
        name = "first bill!",
        url = "some.url",
      )
    )

    sut.deleteBill(bill.id)

    assertEquals(
      expected = 0,
      actual = sut.getBills().size,
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
  ) = DatabaseContentManagementBillManager(database, coroutineContext)
}