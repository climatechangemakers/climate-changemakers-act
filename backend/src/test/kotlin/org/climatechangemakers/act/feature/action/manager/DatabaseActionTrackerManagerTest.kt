package org.climatechangemakers.act.feature.action.manager

import org.climatechangemakers.act.feature.findlegislator.util.suspendTest
import org.climatechangemakers.act.feature.util.DEFAULT_MEMBER_OF_CONGRESS
import org.climatechangemakers.act.feature.util.TestContainerProvider
import org.climatechangemakers.act.feature.util.insertIssue
import org.climatechangemakers.act.feature.util.insertMemberOfCongress
import org.junit.Test
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.assertEquals

class DatabaseActionTrackerManagerTest : TestContainerProvider() {

  private val manager = DatabaseActionTrackerManager(database, EmptyCoroutineContext)

  @Test fun `email action entries are recorded in both tables`() = suspendTest {
    val id = driver.insertIssue("this is an issue", "tweet", "url.com")
    driver.insertMemberOfCongress(DEFAULT_MEMBER_OF_CONGRESS.copy(bioguideId = "hello"))
    manager.trackActionSendEmail("foo@foo.com", "hello", id)

    assertEquals(
      "hello",
      driver.executeQuery(0, "SELECT contacted_bioguide_id FROM action_contact_legislator", 0).let {
        it.next()
        it.getString(0)
      }
    )

    assertEquals(
      id,
      driver.executeQuery(0, "SELECT action_contact_legislator_id FROM action_email_legislator", 0).let {
        it.next()
        it.getLong(0)
      }
    )
  }

  @Test fun `recording a legislator call insert into both tables`() = suspendTest {
    val id = driver.insertIssue("issue", "tweet", "url.com")
    driver.insertMemberOfCongress(DEFAULT_MEMBER_OF_CONGRESS.copy(bioguideId = "bioguide"))
    manager.trackActionPhoneCall("foo@foo.com", "bioguide", id, "8675309")

    assertEquals(
      1,
      driver.executeQuery(0, "SELECT COUNT(*) FROM action_contact_legislator", 0).let {
        it.next()
        it.getLong(0)
      }
    )

    assertEquals(
      1,
      driver.executeQuery(0, "SELECT action_contact_legislator_id FROM action_call_legislator", 0).let {
        it.next()
        it.getLong(0)
      }
    )
  }

  @OptIn(ExperimentalStdlibApi::class)
  @Test fun `recording a tweet inserts into both tables`() = suspendTest {
    val id = driver.insertIssue("issue", "tweet", "url.com")
    driver.insertMemberOfCongress(DEFAULT_MEMBER_OF_CONGRESS.copy(bioguideId = "foo"))
    driver.insertMemberOfCongress(DEFAULT_MEMBER_OF_CONGRESS.copy(bioguideId = "bar"))
    manager.trackTweet("foo@foo.com", listOf("foo", "bar"), id)

    assertEquals(
      2,
      driver.executeQuery(0, "SELECT COUNT(*) FROM action_tweet_legislator", 0).let {
        it.next()
        it.getLong(0)
      },
    )

    assertEquals(
      listOf("foo", "bar"),
      driver.executeQuery(0, "SELECT contacted_bioguide_id FROM action_contact_legislator", 0).let { cursor ->
        buildList {
          cursor.next()
          add(cursor.getString(0))
          cursor.next()
          add(cursor.getString(0))
        }
      }
    )
  }
}