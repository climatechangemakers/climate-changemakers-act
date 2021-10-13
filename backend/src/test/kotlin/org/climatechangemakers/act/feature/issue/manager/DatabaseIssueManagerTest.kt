package org.climatechangemakers.act.feature.issue.manager

import org.climatechangemakers.act.common.model.RepresentedArea
import org.climatechangemakers.act.feature.findlegislator.manager.MemberOfCongressManager
import org.climatechangemakers.act.feature.findlegislator.model.LegislatorPoliticalParty
import org.climatechangemakers.act.feature.findlegislator.model.LegislatorRole
import org.climatechangemakers.act.feature.findlegislator.model.MemberOfCongress
import org.climatechangemakers.act.feature.findlegislator.util.suspendTest
import org.climatechangemakers.act.feature.issue.model.Issue
import org.climatechangemakers.act.feature.issue.model.TalkingPoint
import org.climatechangemakers.act.feature.util.TestContainerProvider
import org.junit.Test
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DatabaseIssueManagerTest : TestContainerProvider() {

  private val fakeMemberOfCongressManager = MemberOfCongressManager { bioguideId ->
    MemberOfCongress(
      bioguideId = bioguideId,
      fullName = "name",
      legislativeRole = LegislatorRole.Representative,
      representedArea = RepresentedArea.Virginia,
      congressionalDistrict = null,
      party = LegislatorPoliticalParty.Democrat,
      dcPhoneNumber = "1",
      twitterHandle = "handle",
      cwcOfficeCode = null,
    )
  }
  private val issueManager = DatabaseIssueManager(fakeMemberOfCongressManager, database, EmptyCoroutineContext)

  @Test fun `getting focus issue returns most recently focused item`() = suspendTest {
    insertIssue(1, "foo", "tweet")
    insertIssue(2, "bar", "tweet")
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
    insertIssue(1, "foo", "tweet")
    insertIssue(2, "bar", "tweet")
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
    insertIssue(1, "foo", "tweet")
    insertIssue(2, "bar", "tweet")
    insertExampleWhyStatement(1, "This is correct")
    insertExampleWhyStatement(2, "this is incorrect")

    assertEquals(
      listOf("This is correct"),
      issueManager.getExampleStatementsForIssue(1),
    )
  }

  @Test fun `example statements query produces max 5 results`() = suspendTest {
    insertIssue(1, "foo", "tweet")
    repeat(10) { insertExampleWhyStatement(1, "$it") }

    assertEquals(
      5,
      issueManager.getExampleStatementsForIssue(1).size,
    )
  }

  @Test fun `example statements throws for a non-existent issueId`() = suspendTest {
    assertFailsWith<NoSuchElementException> {
      issueManager.getExampleStatementsForIssue(100L)
    }
  }

  @Test fun `precomposed tweet is formatted correctly`() = suspendTest {
    insertIssue(1, "issue", "This is a tweet to %s")
    val tweet = issueManager.getPreComposedTweetForIssue(1, listOf("id"))
    assertEquals("This is a tweet to @handle", tweet)
  }

  private fun insertIssue(id: Long, title: String, precomposedTweet: String) {
    driver.execute(0, "INSERT INTO issue(id, title, precomposed_tweet_template) VALUES(?,?,?)", 2) {
      bindLong(1, id)
      bindString(2, title)
      bindString(3, precomposedTweet)
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