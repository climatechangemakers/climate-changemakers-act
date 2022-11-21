package org.climatechangemakers.act.feature.cms.manager.issue

import app.cash.sqldelight.db.SqlDriver
import org.climatechangemakers.act.database.Database
import org.climatechangemakers.act.feature.cms.model.issue.ContentManagementIssue
import org.climatechangemakers.act.feature.findlegislator.util.suspendTest
import org.climatechangemakers.act.feature.util.TestContainerProvider
import org.climatechangemakers.act.feature.util.insertIssue
import org.junit.Test
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
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
          isFocusIssue = false,
        )
      ),
      actual = manager().getIssues(),
    )
  }

  @Test fun `update throws NoSuchElementException with no matching issue ID`() = suspendTest {
    assertFailsWith<NoSuchElementException> {
      manager().updateIssue(
        issue = ContentManagementIssue(
          id = -1,
          title = "does not exist",
          precomposedTweetTemplate = "tweet",
          imageUrl = "image.url",
          description = "description",
          isFocusIssue = false,
        )
      )
    }
  }

  @Test fun `update does not insert into focus ledger isFocusIssue false`() = suspendTest {
    val issueId = driver.insertIssue(
      title = "does not exist",
      precomposedTweet = "tweet",
      imageUrl = "image.url",
      description = "description",
    )
    manager().updateIssue(
      issue = ContentManagementIssue(
        id = issueId,
        title = "does not exist",
        precomposedTweetTemplate = "tweet",
        imageUrl = "image.url",
        description = "description",
        isFocusIssue = false,
      )
    )
    assertEquals(expected = 0, actual = driver.countFocusIssueLedger())
  }

  @Test fun `update inserts into focus ledger isFocusIssue true`() = suspendTest {
    val issueId = driver.insertIssue(
      title = "this is an issue",
      precomposedTweet = "tweet",
      imageUrl = "image.url",
      description = "description",
    )
    val result = manager().updateIssue(
      issue = ContentManagementIssue(
        id = issueId,
        title = "this is another issue",
        precomposedTweetTemplate = "tweet 2",
        imageUrl = "image.url 2",
        description = "description 2",
        isFocusIssue = false,
      )
    )
    assertEquals(
      actual = result,
      expected = ContentManagementIssue(
        id = issueId,
        title = "this is another issue",
        precomposedTweetTemplate = "tweet 2",
        imageUrl = "image.url 2",
        description = "description 2",
        isFocusIssue = false,
      ),
    )
  }

  @Test fun `updates issue`() = suspendTest {
    val issueId = driver.insertIssue(
      title = "does not exist",
      precomposedTweet = "tweet",
      imageUrl = "image.url",
      description = "description",
    )
    manager().updateIssue(
      issue = ContentManagementIssue(
        id = issueId,
        title = "does not exist",
        precomposedTweetTemplate = "tweet",
        imageUrl = "image.url",
        description = "description",
        isFocusIssue = false,
      )
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