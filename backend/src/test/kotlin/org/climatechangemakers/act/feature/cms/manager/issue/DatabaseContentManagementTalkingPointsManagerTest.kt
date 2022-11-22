package org.climatechangemakers.act.feature.cms.manager.issue

import org.climatechangemakers.act.database.Database
import org.climatechangemakers.act.feature.findlegislator.util.suspendTest
import org.climatechangemakers.act.feature.util.TestContainerProvider
import org.climatechangemakers.act.feature.util.insertIssue
import org.climatechangemakers.act.feature.util.insertTalkingPoint
import org.junit.Test
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

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

  private fun manager(
    database: Database = this.database,
    coroutineContext: CoroutineContext = EmptyCoroutineContext,
  ) = DatabaseContentManagementTalkingPointsManager(database, coroutineContext)
}