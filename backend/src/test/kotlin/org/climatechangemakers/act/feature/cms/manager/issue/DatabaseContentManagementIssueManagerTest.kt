package org.climatechangemakers.act.feature.cms.manager.issue

import org.climatechangemakers.act.database.Database
import org.climatechangemakers.act.feature.cms.model.issue.ContentManagementIssue
import org.climatechangemakers.act.feature.findlegislator.util.suspendTest
import org.climatechangemakers.act.feature.util.TestContainerProvider
import org.climatechangemakers.act.feature.util.insertIssue
import org.junit.Test
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.assertEquals

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

  private fun manager(
    coroutineContext: CoroutineContext = EmptyCoroutineContext,
    database: Database = this.database
  ) = DatabaseContentManagementIssueManager(database, coroutineContext)
}