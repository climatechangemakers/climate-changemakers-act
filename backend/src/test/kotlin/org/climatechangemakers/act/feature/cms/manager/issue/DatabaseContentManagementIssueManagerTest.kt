package org.climatechangemakers.act.feature.cms.manager.issue

import app.cash.sqldelight.db.SqlDriver
import org.climatechangemakers.act.database.Database
import org.climatechangemakers.act.feature.bill.model.BillType
import org.climatechangemakers.act.feature.cms.model.issue.ContentManagementIssue
import org.climatechangemakers.act.feature.cms.model.issue.ContentManagementTalkingPoint
import org.climatechangemakers.act.feature.findlegislator.util.suspendTest
import org.climatechangemakers.act.feature.util.TestContainerProvider
import org.climatechangemakers.act.feature.util.insertIssue
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class DatabaseContentManagementIssueManagerTest : TestContainerProvider() {

  @Test fun `manager selects only active issues`() = suspendTest {
    driver.insertIssue(
      title = "foo",
      precomposedTweet = "some tweet",
      imageUrl = "image.url",
      isActive = false,
    )
    driver.insertIssue(
      title = "bar",
      precomposedTweet = "some tweet",
      imageUrl = "image.url",
      isActive = true,
    )

    assertEquals(
      expected = listOf(
        ContentManagementIssue(
          id = 2,
          title = "bar",
          precomposedTweetTemplate = "some tweet",
          imageUrl = "image.url",
          description = "description",
          relatedBillIds = emptyList(),
          talkingPoints = emptyList(),
          isFocusIssue = false,
        )
      ),
      actual = manager().getIssues(),
    )
  }

  @Test fun `manager selects related bills`() = suspendTest {
    val issueId = driver.insertIssue(
      title = "bar",
      precomposedTweet = "some tweet",
      imageUrl = "image.url",
      isActive = true,
    )
    val billId =  database.congressBillQueries.insert(
      congressionalSession = 117,
      billName = "some Bill",
      billType = BillType.HouseBill,
      billNumber = 1,
      url = "fo.com",
    ).executeAsOne().id

    database.congressBillAndIssueQueries.insert(issueId, billId)

    assertEquals(
      expected = listOf(
        ContentManagementIssue(
          id = 1,
          title = "bar",
          precomposedTweetTemplate = "some tweet",
          imageUrl = "image.url",
          description = "description",
          relatedBillIds = listOf(billId),
          talkingPoints = emptyList(),
          isFocusIssue = false,
        )
      ),
      actual = manager().getIssues(),
    )
  }

  @Test fun `manager selects related talking points`() = suspendTest {
    val issueId = driver.insertIssue(
      title = "bar",
      precomposedTweet = "some tweet",
      imageUrl = "image.url",
      isActive = true,
    )
    database.talkingPointQueries.insert(
      title = "some title",
      issueId = issueId,
      content = "blahhh",
      relativeOrderPosition = 0,
    )

    assertEquals(
      expected = listOf(
        ContentManagementIssue(
          id = 1,
          title = "bar",
          precomposedTweetTemplate = "some tweet",
          imageUrl = "image.url",
          description = "description",
          relatedBillIds = emptyList(),
          talkingPoints = listOf(
            ContentManagementTalkingPoint(
              title = "some title",
              content = "blahhh",
              relativeOrderPosition = 0,
            )
          ),
          isFocusIssue = false,
        )
      ),
      actual = manager().getIssues(),
    )
  }

  @Test fun `creating issue throws with issue ID`() = suspendTest {
    val issue = ContentManagementIssue(
      id = 1L,
      title = "title",
      precomposedTweetTemplate = "tweet",
      imageUrl = "foo.url",
      description = "description",
      isFocusIssue = false,
      talkingPoints = emptyList(),
      relatedBillIds = emptyList()
    )

    assertFailsWith<IllegalArgumentException> {
      manager().createIssue(issue)
    }
  }

  @Test fun `creating issue inserts issue and bill relations`() = suspendTest {
    val bill = database.congressBillQueries.insert(
      congressionalSession = 1,
      billType = BillType.HouseBill,
      billNumber = 1,
      billName = "name",
      url = "url"
    ).executeAsOne()
    val issue = ContentManagementIssue(
      id = null,
      title = "title",
      precomposedTweetTemplate = "tweet",
      imageUrl = "foo.url",
      description = "description",
      isFocusIssue = false,
      talkingPoints = emptyList(),
      relatedBillIds = listOf(bill.id),
    )

    val createdIssue = manager().createIssue(issue)

    assertEquals(
      expected = listOf(bill.id),
      actual = createdIssue.relatedBillIds,
    )

    assertEquals(
      expected = listOf(bill.id),
      actual = database.congressBillAndIssueQueries
        .selectBillsForIssueId(createdIssue.id!!)
        .executeAsList()
        .map { it.congress_bill_id },
    )
  }

  @Test fun `creating issue inserts issue and talking points`() = suspendTest {
    val issue = ContentManagementIssue(
      id = null,
      title = "title",
      precomposedTweetTemplate = "tweet",
      imageUrl = "foo.url",
      description = "description",
      isFocusIssue = false,
      talkingPoints = listOf(
        ContentManagementTalkingPoint(
          title = "title",
          content = "content",
          relativeOrderPosition = 0,
        )
      ),
      relatedBillIds = emptyList(),
    )

    val createdIssue = manager().createIssue(issue)

    assertEquals(
      expected = listOf(
        ContentManagementTalkingPoint(
          title = "title",
          content = "content",
          relativeOrderPosition = 0,
        )
      ),
      actual = createdIssue.talkingPoints,
    )

    assertEquals(
      expected = 1,
      actual = database.talkingPointQueries
        .selectForIssueId(createdIssue.id!!)
        .executeAsList()
        .size,
    )
  }

  @Test fun `creating focused issue inserts issue and focus ledger item`() = suspendTest {
    val issue = ContentManagementIssue(
      id = null,
      title = "title",
      precomposedTweetTemplate = "tweet",
      imageUrl = "foo.url",
      description = "description",
      isFocusIssue = true,
      talkingPoints = emptyList(),
      relatedBillIds = emptyList(),
    )

    val createdIssue = manager().createIssue(issue)

    assertTrue(createdIssue.isFocusIssue)

    assertEquals(
      expected = createdIssue.id,
      actual = database.issueAndFocusQueries.selectActiveFocusIssue().executeAsOne().id,
    )
  }

  @Test fun `no issue is created invalid bill`() = suspendTest {
    val issue = ContentManagementIssue(
      id = null,
      title = "title",
      precomposedTweetTemplate = "tweet",
      imageUrl = "foo.url",
      description = "description",
      isFocusIssue = false,
      talkingPoints = emptyList(),
      relatedBillIds = listOf(-1L),
    )

    assertFails { manager().createIssue(issue) }

    assertEquals(
      expected = emptyList(),
      actual = database.issueAndFocusQueries.selectAllActive().executeAsList(),
    )
  }

  @Test fun `no issue is created invalid talking point`() = suspendTest {
    val issue = ContentManagementIssue(
      id = null,
      title = "title",
      precomposedTweetTemplate = "tweet",
      imageUrl = "foo.url",
      description = "description",
      isFocusIssue = false,
      talkingPoints = listOf(
        ContentManagementTalkingPoint(
          title = "title",
          content = "content",
          relativeOrderPosition = 0,
        ),
        ContentManagementTalkingPoint(
          title = "title",
          content = "content",
          relativeOrderPosition = 0,
        ),
      ),
      relatedBillIds = emptyList(),
    )

    assertFails { manager().createIssue(issue) }

    assertEquals(
      expected = emptyList(),
      actual = database.issueAndFocusQueries.selectAllActive().executeAsList(),
    )
  }

  private fun manager(
    coroutineContext: CoroutineContext = EmptyCoroutineContext,
    database: Database = this.database
  ) = DatabaseContentManagementIssueManager(database, coroutineContext)
}

private fun SqlDriver.countFocusIssueLedger(): Long {
  return executeQuery(
    identifier = null,
    sql = "SELECT COUNT(*) FROM focus_issue;",
    mapper = { cursor -> cursor.also { it.next() }.getLong(0)!! },
    parameters = 0,
  ).value
}