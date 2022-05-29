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

  @Test fun `email action entries are recorded action_contact_legislator table`() = suspendTest {
    val id = driver.insertIssue("this is an issue", "tweet", "url.com")
    driver.insertMemberOfCongress(DEFAULT_MEMBER_OF_CONGRESS.copy(bioguideId = "hello"))
    manager.trackActionSendEmail("foo@foo.com", "hello", id, "someDeliveryId")

    assertEquals(
      expected = "hello",
      actual = driver.executeQuery(
        identifier = 0,
        sql = "SELECT contacted_bioguide_id FROM action_contact_legislator",
        mapper = { cursor -> cursor.also { it.next() }.getString(0) },
        parameters = 0,
      )
    )
  }

  @Test fun `email action entries are recorded in action_email_legislator table`() = suspendTest {
    val id = driver.insertIssue("this is an issue", "tweet", "url.com")
    driver.insertMemberOfCongress(DEFAULT_MEMBER_OF_CONGRESS.copy(bioguideId = "hello"))
    manager.trackActionSendEmail("foo@foo.com", "hello", id, "someDeliveryId")

    assertEquals(
      expected = 1,
      driver.executeQuery(
        identifier = 0,
        sql = "SELECT COUNT(*) FROM action_email_legislator",
        mapper = { cursor -> cursor.also { it.next() }.getLong(0) },
        parameters = 0,
      )
    )
  }
  @Test fun `email action entries record associated delivery id`() = suspendTest {
    val id = driver.insertIssue("this is an issue", "tweet", "url.com")
    driver.insertMemberOfCongress(DEFAULT_MEMBER_OF_CONGRESS.copy(bioguideId = "hello"))
    manager.trackActionSendEmail("foo@foo.com", "hello", id, "someDeliveryId")

    assertEquals(
      expected = "someDeliveryId",
      driver.executeQuery(
        identifier = 0,
        sql = "SELECT email_delivery_id FROM action_email_legislator",
        mapper = { cursor -> cursor.also { it.next() }.getString(0) },
        parameters = 0,
      )
    )
  }

  @Test fun `recording a legislator call inserts into action_contact_legislator`() = suspendTest {
    val id = driver.insertIssue("issue", "tweet", "url.com")
    driver.insertMemberOfCongress(DEFAULT_MEMBER_OF_CONGRESS.copy(bioguideId = "bioguide"))
    manager.trackActionPhoneCall("foo@foo.com", "bioguide", id)

    assertEquals(
      expected = "bioguide",
      actual = driver.executeQuery(
        identifier = 0,
        sql = "SELECT contacted_bioguide_id FROM action_contact_legislator",
        mapper = { cursor -> cursor.also { it.next() }.getString(0) },
        parameters = 0,
      ),
    )
  }
  @Test fun `recording a legislator call inserts into action_call_legislator`() = suspendTest {
    val id = driver.insertIssue("issue", "tweet", "url.com")
    driver.insertMemberOfCongress(DEFAULT_MEMBER_OF_CONGRESS.copy(bioguideId = "bioguide"))
    manager.trackActionPhoneCall("foo@foo.com", "bioguide", id)
    assertEquals(
      expected = 1,
      actual = driver.executeQuery(
        identifier = 0,
        sql = "SELECT COUNT(*) FROM action_call_legislator",
        mapper = { cursor -> cursor.also { it.next() }.getLong(0) },
        parameters =  0,
      )
    )
  }

  @Test fun `recording a tweet inserts into both tables`() = suspendTest {
    val id = driver.insertIssue("issue", "tweet", "url.com")
    driver.insertMemberOfCongress(DEFAULT_MEMBER_OF_CONGRESS.copy(bioguideId = "foo"))
    driver.insertMemberOfCongress(DEFAULT_MEMBER_OF_CONGRESS.copy(bioguideId = "bar"))
    manager.trackTweet("foo@foo.com", listOf("foo", "bar"), id)

    assertEquals(
      expected = listOf("foo", "bar"),
      actual = driver.executeQuery(
        identifier = 0,
        sql = "SELECT contacted_bioguide_id FROM action_contact_legislator",
        mapper = { cursor ->
          buildList<String?> {
            cursor.next()
            add(cursor.getString(0))
            cursor.next()
            add(cursor.getString(0))
          }
        },
        parameters = 0,
      )
    )
  }
  @Test fun `recording a tweet inserts action_tweet_legislator`() = suspendTest {
    val id = driver.insertIssue("issue", "tweet", "url.com")
    driver.insertMemberOfCongress(DEFAULT_MEMBER_OF_CONGRESS.copy(bioguideId = "foo"))
    driver.insertMemberOfCongress(DEFAULT_MEMBER_OF_CONGRESS.copy(bioguideId = "bar"))
    manager.trackTweet("foo@foo.com", listOf("foo", "bar"), id)

    assertEquals(
      expected = 2,
      actual = driver.executeQuery(
        identifier = 0,
        sql = "SELECT COUNT(*) FROM action_tweet_legislator",
        mapper = { cursor -> cursor.also { it.next() }.getLong(0) },
        parameters =  0,
      )
    )
  }

  @Test fun `recording a sign up inserts into the right table`() = suspendTest {
    manager.trackActionSignUp("foo@foo.com")
    assertEquals(
      expected = 1,
      actual = driver.executeQuery(
        identifier = 0,
        sql = "SELECT COUNT(*) FROM action_sign_up",
        mapper = { cursor -> cursor.also { it.next() }.getLong(0) },
        parameters =  0,
      )
    )
  }
}