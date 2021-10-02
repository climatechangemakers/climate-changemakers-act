package org.climatechangemakers.act.feature.issue.manager

import org.climatechangemakers.act.feature.findlegislator.util.suspendTest
import org.climatechangemakers.act.feature.issue.model.Issue
import org.climatechangemakers.act.feature.issue.model.TalkingPoint
import org.climatechangemakers.act.feature.util.TestContainerProvider
import org.junit.Test
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.assertEquals

class DatabaseIssueManagerTest : TestContainerProvider() {

  private val issueManager = DatabaseIssueManager(database, EmptyCoroutineContext)

  @Test fun `getting focus issue returns most recently focused item`() = suspendTest {
    insertIssue(1, "foo")
    insertIssue(2, "bar")
    insertTalkingPoint(1, "foo talking point", "foo is cool")
    insertTalkingPoint(2, "bar talking point", "bar is cool")

    focusIssue(1)
    assertEquals(
      Issue(
        1,
        "foo",
        listOf(TalkingPoint("foo talking point", "foo is cool"))
      ),
      issueManager.getFocusIssue()
    )

    focusIssue(2)
    assertEquals(
      Issue(
        2,
        "bar",
        listOf(TalkingPoint("bar talking point", "bar is cool"))
      ),
      issueManager.getFocusIssue()
    )
  }

  @Test fun `getting unfocused issues returns correct values`() = suspendTest {
    insertIssue(1, "foo")
    insertIssue(2, "bar")
    insertTalkingPoint(1, "foo talking point", "foo is cool")
    insertTalkingPoint(2, "bar talking point", "bar is cool")
    focusIssue(1)

    assertEquals(
      listOf(
        Issue(
          2,
          "bar",
          listOf(TalkingPoint("bar talking point", "bar is cool"))
        )
      ),
      issueManager.getUnfocusedIssues()
    )
  }

  @Test fun `example statements for issue are correct`() = suspendTest {
    insertIssue(1, "foo")
    insertIssue(2, "bar")
    insertExampleWhyStatement(1, "This is correct")
    insertExampleWhyStatement(2, "this is incorrect")

    assertEquals(
      listOf("This is correct"),
      issueManager.getExampleStatementsForIssue(1),
    )
  }

  @Test fun `example statements query produces max 5 results`() = suspendTest {
    insertIssue(1, "foo")
    repeat(10) { insertExampleWhyStatement(1, "$it") }

    assertEquals(
      5,
      issueManager.getExampleStatementsForIssue(1).size,
    )
  }

  private fun insertIssue(id: Long, title: String) {
    driver.execute(0, "INSERT INTO issue(id, title) VALUES(?,?)", 2) {
      bindLong(1, id)
      bindString(2, title)
    }
  }

  private fun insertExampleWhyStatement(issueId: Long, statement: String) {
    driver.execute(0, "INSERT INTO example_issue_why_statement(issue_id, statement) VALUES(?,?)", 2) {
      bindLong(1, issueId)
      bindString(2, statement)
    }
  }

  private fun insertTalkingPoint(issueId: Long, title: String, content: String) {
    driver.execute(
      0,
      "INSERT INTO talking_point(issue_id, title, content) VALUES (?,?,?)",
      3,
    ) {
      bindLong(1, issueId)
      bindString(2, title)
      bindString(3, content)
    }
  }

  private fun focusIssue(issueId: Long) {
    driver.execute(0, "INSERT INTO focus_issue(issue_id) VALUES (?)", 1) {
      bindLong(1, issueId)
    }
  }
}