package org.climatechangemakers.act.feature.membership.manager

import okhttp3.MediaType
import okhttp3.ResponseBody
import org.climatechangemakers.act.feature.action.manager.FakeActionTrackerManager
import org.climatechangemakers.act.feature.findlegislator.util.suspendTest
import org.climatechangemakers.act.feature.membership.model.AirtableRecord
import org.climatechangemakers.act.feature.membership.model.AirtableResponse
import org.climatechangemakers.act.feature.membership.service.AirtableFormula
import org.climatechangemakers.act.feature.membership.service.AirtableService
import org.climatechangemakers.act.feature.membership.service.FakeAirtableService
import org.junit.Test
import retrofit2.Response
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

  @Test fun `checkMembership retries`() = suspendTest {
    val fakeService = object : AirtableService {

      private var hasFailed = false

      override suspend fun checkMembership(formula: AirtableFormula.FilterByEmailFormula): Response<AirtableResponse> {
        val result = if (hasFailed) {
          Response.success(AirtableResponse(listOf(AirtableRecord(formula.email))))
        } else {
          Response.error(429, ResponseBody.create(MediaType.get("application/json"), "Failed"))
        }

        hasFailed = !hasFailed

        return result
      }
    }
    val manager = AirtableMembershipManager(FakeActionTrackerManager(), fakeService)
    assertTrue(manager.checkMembership("foo@bar.com"))
  }
}