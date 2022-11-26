package org.climatechangemakers.act.feature.cms.manager.issue

import org.climatechangemakers.act.common.extension.state
import org.climatechangemakers.act.database.Database
import org.climatechangemakers.act.feature.cms.model.issue.ContentManagementTalkingPoint
import org.climatechangemakers.act.feature.findlegislator.util.suspendTest
import org.climatechangemakers.act.feature.util.TestContainerProvider
import org.climatechangemakers.act.feature.util.insertIssue
import org.climatechangemakers.act.feature.util.insertTalkingPoint
import org.junit.Test
import org.postgresql.util.PSQLException
import org.postgresql.util.PSQLState
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class DatabaseContentManagementTalkingPointsManagerTest : TestContainerProvider() {

  @Test fun `manager throws NSE issue ID does not exist`() = suspendTest {
    assertFailsWith<NoSuchElementException> {
      manager().getTalkingPoints(-1)
    }
  }

  @Test fun `manager returns talking points for an issue`() = suspendTest {
    val issueId = driver.insertIssue(
      title = "issue",
      precomposedTweet = "tweet",
      imageUrl = "image.url",
      description = "description",
    )
    val issue2Id = driver.insertIssue(
      title = "other issue",
      precomposedTweet = "tweet",
      imageUrl = "image.url",
      description = "description",
    )

    driver.insertTalkingPoint(issueId, "talking point title", "content", 1)
    driver.insertTalkingPoint(issue2Id, "talking point title 2", "content 2", 1)

    assertEquals(
      expected = listOf("talking point title"),
      actual = manager().getTalkingPoints(issueId).map { it.title },
    )
  }

  @Test fun `manager returns updated talking points for issue`() = suspendTest {
    val issueId = driver.insertIssue(
      title = "issue",
      precomposedTweet = "tweet",
      imageUrl = "image.url",
      description = "description",
    )
    val talkingPoints = listOf(
      ContentManagementTalkingPoint(
        id = null,
        title = "some title",
        content = "some content",
        relativeOrderPosition = 1,
      )
    )
    val ret = manager().updateTalkingPoints(
      issueId = issueId,
      talkingPoints = talkingPoints,
    )

    assertEquals(
      expected = talkingPoints.map { it.title },
      actual = ret.map { it.title },
    )
    assertEquals(
      expected = talkingPoints.map { it.content },
      actual = ret.map { it.content },
    )
    assertEquals(
      expected = talkingPoints.map { it.relativeOrderPosition},
      actual = ret.map { it.relativeOrderPosition },
    )
    assertTrue(ret.all { it.id != null })
  }

  @Test fun `manager adds new talking points`() = suspendTest {
    val issueId = driver.insertIssue(
      title = "issue",
      precomposedTweet = "tweet",
      imageUrl = "image.url",
      description = "description",
    )
    assertEquals(expected = 0, actual = manager().getTalkingPoints(issueId).size)
    val talkingPoints = listOf(
      ContentManagementTalkingPoint(
        id = null,
        title = "some title",
        content = "some content",
        relativeOrderPosition = 1,
      )
    )
    assertEquals(
      expected = 1,
      actual = manager().updateTalkingPoints(
        issueId = issueId,
        talkingPoints = talkingPoints,
      ).size
    )
  }

  @Test fun `manager deletes omitted talking points`() = suspendTest {
    val issueId = driver.insertIssue(
      title = "issue",
      precomposedTweet = "tweet",
      imageUrl = "image.url",
      description = "description",
    )
    driver.insertTalkingPoint(issueId, "title", "content", relativeOrdering = 1)
    assertEquals(
      expected = emptyList(),
      actual = manager().updateTalkingPoints(
        issueId = issueId,
        talkingPoints = emptyList(),
      ),
    )
  }

  @Test fun `manager updates altered talking points`() = suspendTest {
    val issueId = driver.insertIssue(
      title = "issue",
      precomposedTweet = "tweet",
      imageUrl = "image.url",
      description = "description",
    )
    val tpId = driver.insertTalkingPoint(issueId, "title", "content", relativeOrdering = 1)
    assertEquals(
      expected = listOf("updated title"),
      actual = manager().updateTalkingPoints(
        issueId = issueId,
        talkingPoints = listOf(
          ContentManagementTalkingPoint(
            id = tpId,
            title = "updated title",
            content = "content",
            relativeOrderPosition = 1,
          )
        ),
      ).map { it.title }
    )
  }

  @Test fun `manager errors altering talking points on nonexistent issue`() = suspendTest {
    val talkingPoints = listOf(
      ContentManagementTalkingPoint(
        id = null,
        title = "some title",
        content = "some content",
        relativeOrderPosition = 1,
      )
    )

    val t = assertFailsWith<PSQLException> {
      manager().updateTalkingPoints(
        issueId = -1,
        talkingPoints = talkingPoints,
      )
    }
    assertEquals(
      expected = PSQLState.FOREIGN_KEY_VIOLATION,
      actual = t.state
    )
  }

  private fun manager(
    database: Database = this.database,
    coroutineContext: CoroutineContext = EmptyCoroutineContext,
  ) = DatabaseContentManagementTalkingPointsManager(database, coroutineContext)
}