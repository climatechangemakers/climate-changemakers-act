package org.climatechangemakers.act.feature.cms.manager.issue

import app.cash.sqldelight.db.SqlDriver
import org.climatechangemakers.act.common.columnadapter.StringEnumColumnAdapter
import org.climatechangemakers.act.database.Database
import org.climatechangemakers.act.feature.bill.model.Bill
import org.climatechangemakers.act.feature.bill.model.BillType
import org.climatechangemakers.act.feature.cms.model.bill.CreateBill
import org.climatechangemakers.act.feature.findlegislator.util.suspendTest
import org.climatechangemakers.act.feature.util.TestContainerProvider
import org.climatechangemakers.act.feature.util.insertIssue
import org.junit.Test
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DatabaseIssueAndBillManagerTest : TestContainerProvider() {

  @Test fun `throws NSE invalid issue id`() = suspendTest {
    assertFailsWith<NoSuchElementException> {
      manager().getBillsForIssueId(-1)
    }
  }

  @Test fun `returns bills associated with an issue`() = suspendTest {
    val billId = driver.insertBill(
      bill = CreateBill(congressionalSession = 1, type = BillType.HouseBill, number = 1, name = "a", url = "a")
    )
    val issueId = driver.insertIssue(
      title = "issue",
      precomposedTweet = "tweet",
      imageUrl = "url",
      description = "description",
    )

    driver.insertBillAndIssueAssociation(issueId, billId)

    assertEquals(
      expected = listOf(
        Bill(id = billId, congressionalSession = 1, type = BillType.HouseBill, number = 1, name = "a", url = "a")
      ),
      actual = manager().getBillsForIssueId(issueId)
    )
  }

  private fun manager(
    database: Database = this.database,
    coroutineContext: CoroutineContext = EmptyCoroutineContext,
  ) = DatabaseIssueAndBillAssociationManager(database, coroutineContext)
}

private fun SqlDriver.insertBillAndIssueAssociation(
  issueId: Long,
  billId: Long,
) = execute(
  identifier = null,
  sql = "INSERT INTO congress_bill_and_issue(issue_id, congress_bill_id) VALUES (?,?);",
  parameters = 2,
) {
  bindLong(0, issueId)
  bindLong(1, billId)
}

private fun SqlDriver.insertBill(bill: CreateBill): Long = executeQuery(
  identifier = null,
  sql = """
    | INSERT INTO congress_bill (
    |   congressional_session,
    |   bill_type,
    |   bill_number,
    |   bill_name,
    |   url
    | )
    | VALUES (?,?,?,?,?) RETURNING id;
  """.trimMargin(),
  parameters = 5,
  mapper = { cursor -> cursor.also { it.next() }.getLong(0)!! },
  binders = {
    bindLong(0, bill.congressionalSession.toLong())
    bindString(1, StringEnumColumnAdapter<BillType>().encode(bill.type))
    bindLong(2, bill.number.toLong())
    bindString(3, bill.name)
    bindString(4, bill.url)
  }
).value