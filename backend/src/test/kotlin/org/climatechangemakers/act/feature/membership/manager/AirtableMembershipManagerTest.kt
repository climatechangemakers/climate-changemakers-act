package org.climatechangemakers.act.feature.membership.manager

import okhttp3.MediaType
import okhttp3.ResponseBody
import org.climatechangemakers.act.common.model.RepresentedArea
import org.climatechangemakers.act.feature.action.manager.FakeActionTrackerManager
import org.climatechangemakers.act.feature.email.manager.FakeEmailEnrollmentManager
import org.climatechangemakers.act.feature.findlegislator.util.suspendTest
import org.climatechangemakers.act.feature.membership.model.AirtableCreateRecordRequest
import org.climatechangemakers.act.feature.membership.model.AirtableRecord
import org.climatechangemakers.act.feature.membership.model.AirtableResponse
import org.climatechangemakers.act.feature.membership.service.AirtableFormula
import org.climatechangemakers.act.feature.membership.service.AirtableService
import org.climatechangemakers.act.feature.membership.service.FakeAirtableService
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AirtableMembershipManagerTest {

  private val airtableService = FakeAirtableService()
  private val emailEnrollmentManager = FakeEmailEnrollmentManager()
  private val actionManager = FakeActionTrackerManager()
  private val manager = AirtableMembershipManager(actionManager, airtableService, emailEnrollmentManager)

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

      override suspend fun signUp(body: AirtableCreateRecordRequest) = TODO()
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
    val manager = AirtableMembershipManager(FakeActionTrackerManager(), fakeService, emailEnrollmentManager)
    assertTrue(manager.checkMembership("foo@bar.com"))
  }

  @Test fun `signUp retries exhaustively`() = suspendTest {
    val fakeService = object : AirtableService {

      private var attempt = 0

      override suspend fun checkMembership(formula: AirtableFormula.FilterByEmailFormula) = TODO()
      override suspend fun signUp(body: AirtableCreateRecordRequest): Response<Unit> {
        return Response.error(429, ResponseBody.create(MediaType.get("application/json"), "Failed ${++attempt}"))
      }
    }

    val throwable = assertFailsWith<HttpException> {
      AirtableMembershipManager(FakeActionTrackerManager(), fakeService, emailEnrollmentManager).signUp(
        "foo@bar.com",
        "foo",
        "bar",
        "23231",
        RepresentedArea.Virginia,
        true,
        "Nah",
        "This is an action reason",
        "social media ew",
      )
    }
    assertEquals("Failed 3", throwable.response()?.errorBody()?.string())
  }

  @Test fun `signUp invokes mailchimp`() = suspendTest {
    manager.signUp(
      email = "email@email.com",
      firstName = "foo",
      lastName = "bar",
      postalCode = "12345",
      state = RepresentedArea.Alabama,
      experience = false,
      referral = "This test yo",
      actionReason = "I'm a test!",
      socialVerification = "JUnit verified",
    )

    assertEquals("email@email.com", emailEnrollmentManager.emails.receive())
    assertEquals("foo", emailEnrollmentManager.firstNames.receive())
    assertEquals("bar", emailEnrollmentManager.lastNames.receive())
    assertEquals(RepresentedArea.Alabama, emailEnrollmentManager.states.receive())
  }

  @Test fun `signUp records action`() = suspendTest {
    manager.signUp(
      email = "email@email.com",
      firstName = "foo",
      lastName = "bar",
      postalCode = "12345",
      state = RepresentedArea.Alabama,
      experience = false,
      referral = "This test yo",
      actionReason = "I'm a test!",
      socialVerification = "JUnit verified",
    )

    assertEquals("email@email.com", actionManager.capturedEmail.receive())
  }
}