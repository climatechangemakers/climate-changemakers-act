package org.climatechangemakers.act.feature.issue.manager

import org.climatechangemakers.act.feature.findlegislator.manager.FakeMemberOfCongressManager
import org.climatechangemakers.act.feature.findlegislator.util.suspendTest
import org.climatechangemakers.act.feature.issue.model.Issue
import org.climatechangemakers.act.feature.issue.model.PreComposedTweetResponse
import org.climatechangemakers.act.feature.issue.model.TalkingPoint
import org.climatechangemakers.act.feature.util.TestContainerProvider
import org.climatechangemakers.act.feature.util.insertIssue
import org.junit.Test
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DatabaseIssueManagerTest : TestContainerProvider() {

  private val fakeMemberOfCongressManager = FakeMemberOfCongressManager()
  private val issueManager = DatabaseIssueManager(fakeMemberOfCongressManager, database, EmptyCoroutineContext)

  @Test fun `getting focus issue returns most recently focused item`() = suspendTest {
    val id1 = driver.insertIssue("foo", "tweet", "url.com")
    val id2 = driver.insertIssue("bar", "tweet", "url.com")
    insertTalkingPoint(id1, "foo talking point", "foo is cool", 2)
    insertTalkingPoint(id2, "bar talking point", "bar is cool", 1)

    focusIssue(id1)
    assertEquals(
      Issue(
        id1,
        "foo",
        "url.com",
        "description",
        listOf(TalkingPoint("foo talking point", "foo is cool"))
      ),
      issueManager.getFocusIssue()
    )

    focusIssue(id2)
    assertEquals(
      Issue(
        id2,
        "bar",
        "url.com",
        "description",
        listOf(TalkingPoint("bar talking point", "bar is cool"))
      ),
      issueManager.getFocusIssue()
    )
  }

  @Test fun `getting unfocused issues returns correct values`() = suspendTest {
    val id1 = driver.insertIssue("foo", "tweet", "url.com")
    val id2 = driver.insertIssue("bar", "tweet", "url.com")
    insertTalkingPoint(id1, "foo talking point", "foo is cool", 2)
    insertTalkingPoint(id2, "bar talking point", "bar is cool", 1)
    focusIssue(id1)

    assertEquals(
      listOf(
        Issue(
          id2,
          "bar",
          "url.com",
          "description",
          listOf(TalkingPoint("bar talking point", "bar is cool"))
        )
      ),
      issueManager.getUnfocusedIssues()
    )
  }

  @Test fun `example statements for issue are correct`() = suspendTest {
    val id1 = driver.insertIssue("foo", "tweet", "url.com")
    val id2 = driver.insertIssue("bar", "tweet", "url.com")
    insertExampleWhyStatement(id1, "This is correct")
    insertExampleWhyStatement(id2, "this is incorrect")

    assertEquals(
      listOf("This is correct"),
      issueManager.getExampleStatementsForIssue(id1),
    )
  }

  @Test fun `example statements query produces max 5 results`() = suspendTest {
    val id1 = driver.insertIssue("foo", "tweet", "url.com")
    repeat(10) { insertExampleWhyStatement(id1, "$it") }

    assertEquals(
      5,
      issueManager.getExampleStatementsForIssue(id1).size,
    )
  }

  @Test fun `example statements throws for a non-existent issueId`() = suspendTest {
    assertFailsWith<NoSuchElementException> {
      issueManager.getExampleStatementsForIssue(100L)
    }
  }

  @Test fun `precomposed tweet is formatted correctly`() = suspendTest {
    val id1 = driver.insertIssue("issue", "This is a tweet to %s", "url.com")
    fakeMemberOfCongressManager.twitterHandlesQueue.send(listOf("handle"))
    val tweet = issueManager.getPreComposedTweetForIssue(id1, listOf("id"))
    assertEquals(PreComposedTweetResponse("This is a tweet to @handle"), tweet)
  }

  @Test fun `getting issue title by id gets correct result`() = suspendTest {
    driver.insertIssue("foo", "tweet", "url.com")
    val id2 = driver.insertIssue("bar", "tweet", "url.com")
    val issueTitle = issueManager.getIssueTitleForId(id2)
    assertEquals("bar", issueTitle)
  }

  @Test fun `talking points for an issue are in order`() = suspendTest {
    val id1 = driver.insertIssue("foo", "tweet", "url.com")
    insertTalkingPoint(id1, "foo talking point 2", "foo is cool", 2)
    insertTalkingPoint(id1, "foo talking point 3", "foo is cool", 3)
    insertTalkingPoint(id1, "foo talking point 1", "foo is cool", 1)

    val result = issueManager.getUnfocusedIssues()
    assertEquals(
      (1..3).map { "foo talking point $it" },
      result.first().talkingPoints.map { it.title },
    )
  }

  @Test fun `inactive issues are hidden`() = suspendTest {
    driver.insertIssue("foo", "tweet", "url.com", isActive = false)
    val result = issueManager.getUnfocusedIssues()
    assertEquals(emptyList(), result)
  }

  @Test fun `an inactive focus issue is hidden and will default to next most recently focused issue`() = suspendTest {
    val id1 = driver.insertIssue("foo", "tweet", "url.com", isActive = true)
    val id2 = driver.insertIssue("foo", "tweet", "url.com", isActive = false)
    focusIssue(id1)
    focusIssue(id2)
    assertEquals(id1, issueManager.getFocusIssue().id)
  }

  private fun insertExampleWhyStatement(issueId: Long, statement: String) {
    driver.execute(0, "INSERT INTO example_issue_why_statement(issue_id, statement) VALUES(?,?)", 2) {
      bindLong(0, issueId)
      bindString(1, statement)
    }
  }

  private fun insertTalkingPoint(issueId: Long, title: String, content: String, relativeOrdering: Int) {
    driver.execute(
      0,
      "INSERT INTO talking_point(issue_id, title, content, relative_order_position) VALUES (?,?,?,?)",
      4,
    ) {
      bindLong(0, issueId)
      bindString(1, title)
      bindString(2, content)
      bindLong(3, relativeOrdering.toLong())
    }
  }

  private fun focusIssue(issueId: Long) {
    driver.execute(0, "INSERT INTO focus_issue(issue_id) VALUES (?)", 1) {
      bindLong(0, issueId)
    }
  }
}