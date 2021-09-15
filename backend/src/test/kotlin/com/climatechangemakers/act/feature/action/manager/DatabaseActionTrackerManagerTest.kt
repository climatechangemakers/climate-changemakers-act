package com.climatechangemakers.act.feature.action.manager

import com.climatechangemakers.act.feature.findlegislator.util.suspendTest
import com.climatechangemakers.act.feature.util.TestContainerProvider
import org.junit.Test
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.assertEquals

class DatabaseActionTrackerManagerTest : TestContainerProvider() {

  private val manager = DatabaseActionTrackerManager(database, EmptyCoroutineContext)

  @Test fun `email action entries are recorded for every bioguide`() = suspendTest {
    val bioguides = listOf("hello", "goodbye")
    insertIssue(1, "this is an issue")
    manager.trackActionSendEmails("foo@foo.com", bioguides, 1)

    assertEquals(
      2,
      driver.executeQuery(0, "SELECT COUNT(*) FROM action_contact_legislator", 0).let {
        it.next()
        it.getLong(0)
      }
    )
  }

  @Test fun `recording a legislator call insert into both tables`() = suspendTest {
    insertIssue(1, "issue")
    manager.trackActionPhoneCall("foo@foo.com", "bioguide", 1, "8675309")

    assertEquals(
      1,
      driver.executeQuery(0, "SELECT COUNT(*) FROM action_contact_legislator", 0).let {
        it.next()
        it.getLong(0)
      }
    )

    assertEquals(
      1,
      driver.executeQuery(0, "SELECT COUNT(*) FROM action_call_legislator", 0).let {
        it.next()
        it.getLong(0)
      }
    )
  }

  private fun insertIssue(id: Long, title: String) {
    driver.execute(0, "INSERT INTO issue(id, title) VALUES(?,?)", 2) {
      bindLong(1, id)
      bindString(2, title)
    }
  }
}