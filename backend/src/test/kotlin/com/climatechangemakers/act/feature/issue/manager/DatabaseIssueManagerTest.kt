package com.climatechangemakers.act.feature.issue.manager

import com.climatechangemakers.act.feature.findlegislator.util.suspendTest
import com.climatechangemakers.act.feature.issue.model.Issue
import com.climatechangemakers.act.feature.issue.model.TalkingPoint
import com.climatechangemakers.act.feature.util.TestContainerProvider
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

  private fun insertIssue(id: Long, title: String) {
    driver.execute(0, "INSERT INTO issue(id, title) VALUES(?,?)", 2) {
      bindLong(1, id)
      bindString(2, title)
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