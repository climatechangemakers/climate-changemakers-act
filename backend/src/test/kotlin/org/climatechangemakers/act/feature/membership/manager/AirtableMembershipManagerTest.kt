package org.climatechangemakers.act.feature.membership.manager

import org.climatechangemakers.act.feature.action.manager.FakeActionTrackerManager
import org.climatechangemakers.act.feature.findlegislator.util.suspendTest
import org.climatechangemakers.act.feature.membership.service.FakeAirtableService
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AirtableMembershipManagerTest {

  private val airtableService = FakeAirtableService()
  private val manager = AirtableMembershipManager(FakeActionTrackerManager(), airtableService)

  @Test fun `checkMembership returns true when user record present`() = suspendTest {
    airtableService.registeredMembers.add("foo@bar.com")
    assertTrue(manager.checkMembership("foo@bar.com"))
  }

  @Test fun `checkMembership returns false when user record is not present`() = suspendTest {
    assertFalse(manager.checkMembership("not@present.com"))
  }
}