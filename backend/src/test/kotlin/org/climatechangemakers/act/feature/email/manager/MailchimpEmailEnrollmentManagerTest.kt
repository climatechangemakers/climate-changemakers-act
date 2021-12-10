package org.climatechangemakers.act.feature.email.manager

import org.climatechangemakers.act.common.model.RepresentedArea
import org.climatechangemakers.act.feature.email.model.SubscribeChangemakerRequest
import org.climatechangemakers.act.feature.email.service.FakeMailchimpService
import org.climatechangemakers.act.feature.findlegislator.util.suspendTest
import kotlin.test.Test
import kotlin.test.assertEquals

class MailchimpEmailEnrollmentManagerTest {

  private val service = FakeMailchimpService()
  private val manager = MailchimpEmailEnrollmentManager(service, "some_id")

  @Test fun `manager calls service with correct parameters`() = suspendTest {
    manager.subscribeChangemaker(
      email = "email@email.com",
      firstName = "foo",
      lastName = "bar",
      state = RepresentedArea.Alabama,
    )

    assertEquals("some_id", service.capturedAudienceIds.receive())
    assertEquals(
      SubscribeChangemakerRequest("email@email.com", "foo", "bar", RepresentedArea.Alabama),
      service.capturedRequestBodies.receive(),
    )
  }
}