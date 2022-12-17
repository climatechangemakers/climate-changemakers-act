package org.climatechangemakers.act.feature.cms.manager.issue

import app.cash.sqldelight.db.SqlDriver
import org.climatechangemakers.act.database.Database
import org.climatechangemakers.act.feature.bill.model.BillType
import org.climatechangemakers.act.feature.cms.model.issue.ContentManagementIssue
import org.climatechangemakers.act.feature.cms.model.issue.ContentManagementTalkingPoint
import org.climatechangemakers.act.feature.cms.model.issue.CreateIssue
import org.climatechangemakers.act.feature.findlegislator.util.suspendTest
import org.climatechangemakers.act.feature.util.TestContainerProvider
import org.climatechangemakers.act.feature.util.insertIssue
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

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
    val talkingPointId = database.talkingPointQueries.insert(
      title = "some title",
      issueId = issueId,
      content = "blahhh",
      relativeOrderPosition = 0,
    ).executeAsOne()

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
              id = talkingPointId,
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

//  @Test fun `update throws NoSuchElementException with no matching issue ID`() = suspendTest {
//    assertFailsWith<NoSuchElementException> {
//      manager().updateIssue(
//        issue = ContentManagementIssue(
//          id = -1,
//          title = "does not exist",
//          precomposedTweetTemplate = "tweet",
//          imageUrl = "image.url",
//          description = "description",
//          isFocusIssue = false,
//        )
//      )
//    }
//  }
//
//  @Test fun `update does not insert into focus ledger isFocusIssue false`() = suspendTest {
//    val issueId = driver.insertIssue(
//      title = "does not exist",
//      precomposedTweet = "tweet",
//      imageUrl = "image.url",
//      description = "description",
//    )
//    manager().updateIssue(
//      issue = ContentManagementIssue(
//        id = issueId,
//        title = "does not exist",
//        precomposedTweetTemplate = "tweet",
//        imageUrl = "image.url",
//        description = "description",
//        isFocusIssue = false,
//      )
//    )
//    assertEquals(expected = 0, actual = driver.countFocusIssueLedger())
//  }
//
//  @Test fun `update inserts into focus ledger isFocusIssue true`() = suspendTest {
//    val issueId = driver.insertIssue(
//      title = "does not exist",
//      precomposedTweet = "tweet",
//      imageUrl = "image.url",
//      description = "description",
//    )
//    manager().updateIssue(
//      issue = ContentManagementIssue(
//        id = issueId,
//        title = "does not exist",
//        precomposedTweetTemplate = "tweet",
//        imageUrl = "image.url",
//        description = "description",
//        isFocusIssue = true,
//      )
//    )
//    assertEquals(expected = 1, actual = driver.countFocusIssueLedger())
//  }
//
//  @Test fun `updates issue`() = suspendTest {
//    val issueId = driver.insertIssue(
//      title = "this is an issue",
//      precomposedTweet = "tweet",
//      imageUrl = "image.url",
//      description = "description",
//    )
//    val result = manager().updateIssue(
//      issue = ContentManagementIssue(
//        id = issueId,
//        title = "this is another issue",
//        precomposedTweetTemplate = "tweet 2",
//        imageUrl = "image.url 2",
//        description = "description 2",
//        isFocusIssue = false,
//      )
//    )
//    assertEquals(
//      actual = result,
//      expected = ContentManagementIssue(
//        id = issueId,
//        title = "this is another issue",
//        precomposedTweetTemplate = "tweet 2",
//        imageUrl = "image.url 2",
//        description = "description 2",
//        isFocusIssue = false,
//      ),
//    )
//  }
//
//  @Test fun `create inserts into focus ledger`() = suspendTest {
//    manager().createIssue(
//      issue = CreateIssue(
//        title = "this is another issue",
//        precomposedTweetTemplate = "tweet 2",
//        imageUrl = "image.url 2",
//        description = "description 2",
//        isFocusIssue = true,
//      )
//    )
//    assertEquals(expected = 1, actual = driver.countFocusIssueLedger())
//  }
//
//  @Test fun `create does not insert into focus ledger`() = suspendTest {
//    manager().createIssue(
//      issue = CreateIssue(
//        title = "this is another issue",
//        precomposedTweetTemplate = "tweet 2",
//        imageUrl = "image.url 2",
//        description = "description 2",
//        isFocusIssue = false,
//      )
//    )
//    assertEquals(expected = 0, actual = driver.countFocusIssueLedger())
//  }
//
//  @Test fun `create inserts issue`() = suspendTest {
//    val result = manager().createIssue(
//      issue = CreateIssue(
//        title = "this is another issue",
//        precomposedTweetTemplate = "tweet 2",
//        imageUrl = "image.url 2",
//        description = "description 2",
//        isFocusIssue = false,
//      )
//    )
//    assertEquals(
//      actual = result,
//      expected = ContentManagementIssue(
//        id = 1L,
//        title = "this is another issue",
//        precomposedTweetTemplate = "tweet 2",
//        imageUrl = "image.url 2",
//        description = "description 2",
//        isFocusIssue = false,
//      )
//    )
//  }

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