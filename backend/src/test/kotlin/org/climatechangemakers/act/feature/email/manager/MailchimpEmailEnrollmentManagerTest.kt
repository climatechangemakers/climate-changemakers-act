package org.climatechangemakers.act.feature.email.manager

import okhttp3.MediaType
import okhttp3.ResponseBody
import org.climatechangemakers.act.common.model.RepresentedArea
import org.climatechangemakers.act.feature.email.model.EnrollMemberRequest
import org.climatechangemakers.act.feature.email.model.SubscribeNewsletterRequest
import org.climatechangemakers.act.feature.email.service.FakeMailchimpService
import org.climatechangemakers.act.feature.findlegislator.util.suspendTest
import retrofit2.HttpException
import retrofit2.Response
import kotlin.test.*

class MailchimpEmailEnrollmentManagerTest {

  @Test fun `manager calls service with correct parameters`() = suspendTest {
    val service = FakeMailchimpService { TODO() }
    val manager = MailchimpEmailEnrollmentManager(service, "some_id")
    manager.subscribeChangemaker(
      email = "email@email.com",
      firstName = "foo",
      lastName = "bar",
      state = RepresentedArea.Alabama,
    )

    assertEquals("some_id", service.capturedAudienceIds.receive())
    assertEquals("4f64c9f81bb0d4ee969aaf7b4a5a6f40", service.capturedEmailHashes.receive())
    assertEquals(
      EnrollMemberRequest("email@email.com", "foo", "bar", RepresentedArea.Alabama),
      service.capturedEnrollmentRequestBodies.receive(),
    )
  }

  @Test fun `manager calls service with hash of lowercase email address`() = suspendTest {
    val service = FakeMailchimpService { TODO() }
    val manager = MailchimpEmailEnrollmentManager(service, "some_id")
    manager.subscribeChangemaker(
      email = "EMAIL@email.com",
      firstName = "foo",
      lastName = "bar",
      state = RepresentedArea.Alabama,
    )

    assertEquals("4f64c9f81bb0d4ee969aaf7b4a5a6f40", service.capturedEmailHashes.receive())
  }

  @Test fun `manager calls service with hash of lowercase email newsletter subscribe`() = suspendTest {
    val service = FakeMailchimpService { Response.success(null) }
    val manager = MailchimpEmailEnrollmentManager(service, "some_id")
    manager.subscribeChangemaker(email = "EMAIL@email.com")
    assertEquals("4f64c9f81bb0d4ee969aaf7b4a5a6f40", service.capturedEmailHashes.receive())
  }

  @Test fun `manager newsletter subscribe avoids calling service for existing member`() = suspendTest {
    val service = FakeMailchimpService { Response.success(null) }
    val manager = MailchimpEmailEnrollmentManager(service, "some_id")
    manager.subscribeChangemaker(email = "EMAIL@email.com")
    assertNull( service.capturedEnrollmentRequestBodies.tryReceive().getOrNull())
  }

  @Test fun `manager newsletter subscribe calls service for new member`() = suspendTest {
    val service = FakeMailchimpService {
      Response.error(404, ResponseBody.create(MediaType.get("application/json"), ""))
    }
    val manager = MailchimpEmailEnrollmentManager(service, "some_id")
    manager.subscribeChangemaker(email = "EMAIL@email.com")
    assertEquals(
      SubscribeNewsletterRequest("EMAIL@email.com"),
      service.capturedSubscribeRequestBodies.tryReceive().getOrNull(),
    )
  }

  @Test fun `manager newsletter subscribe throws for non-404 http error`() = suspendTest {
    val service = FakeMailchimpService {
      Response.error(400, ResponseBody.create(MediaType.get("application/json"), ""))
    }
    val manager = MailchimpEmailEnrollmentManager(service, "some_id")
    assertFailsWith<HttpException> {
      manager.subscribeChangemaker(email = "EMAIL@email.com")
    }
  }
}