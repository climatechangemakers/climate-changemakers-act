package org.climatechangemakers.act.feature.bill.manager

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.climatechangemakers.act.common.util.FakeClock
import org.climatechangemakers.act.database.Database
import org.climatechangemakers.act.feature.bill.model.BillType
import org.climatechangemakers.act.feature.findlegislator.util.suspendTest
import org.climatechangemakers.act.feature.util.TestContainerProvider
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.Test
import kotlin.test.assertEquals

class DatabaseBillManagerTest : TestContainerProvider() {

  @Test fun `returns correct bills`() = suspendTest {
    val bill = database.congressBillQueries.insert(
      congressionalSession = 117,
      billType = BillType.HouseBill,
      billNumber = 1234,
      billName = "some name",
      url = "some.url",
    ).executeAsOne()
    val bill2 = database.congressBillQueries.insert(
      congressionalSession = 116,
      billType = BillType.HouseBill,
      billNumber = 5678,
      billName = "some name",
      url = "some.url",
    ).executeAsOne()
    val issueId = database.issueQueries.insertIssue(
      title = "some issue",
      precomposedTweet = "tweet",
      imageUrl = "some.image",
      description = "description",
    ).executeAsOne()
    database.congressBillAndIssueQueries.insert(issueId = issueId, billId = bill.id)
    database.congressBillAndIssueQueries.insert(issueId = issueId, billId = bill2.id)

    val dec152022 = Instant.fromEpochSeconds(1671147752L)
    val manager = manager(clock = FakeClock(dec152022))

    assertEquals(
      expected = 1,
      actual = manager.getBillsForIssueId(issueId).size,
    )
  }

  private fun manager(
    clock: Clock = FakeClock(Instant.fromEpochMilliseconds(0)),
    database: Database = this.database,
  ) = DatabaseBillManager(clock, database, EmptyCoroutineContext)
}
