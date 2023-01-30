package org.climatechangemakers.act.feature.communicatewithcongress.manager

import org.climatechangemakers.act.common.model.Failure
import org.climatechangemakers.act.common.model.RepresentedArea
import org.climatechangemakers.act.common.model.Success
import org.climatechangemakers.act.feature.action.manager.ActionTrackerManager
import org.climatechangemakers.act.feature.action.manager.FakeActionTrackerManager
import org.climatechangemakers.act.feature.action.model.SendEmailRequest
import org.climatechangemakers.act.feature.bill.manager.BillManager
import org.climatechangemakers.act.feature.bill.manager.FakeBillManager
import org.climatechangemakers.act.feature.communicatewithcongress.model.Prefix
import org.climatechangemakers.act.feature.communicatewithcongress.model.Topic
import org.climatechangemakers.act.feature.communicatewithcongress.service.FakeCommunicateWithCongressService
import org.climatechangemakers.act.feature.communicatewithcongress.service.HouseCommunicateWithCongressService
import org.climatechangemakers.act.feature.communicatewithcongress.service.SenateCommunicateWithCongressService
import org.climatechangemakers.act.feature.findlegislator.manager.FakeMemberOfCongressManager
import org.climatechangemakers.act.feature.findlegislator.manager.MemberOfCongressManager
import org.climatechangemakers.act.feature.findlegislator.model.LegislatorPoliticalParty
import org.climatechangemakers.act.feature.findlegislator.model.LegislatorRole
import org.climatechangemakers.act.feature.findlegislator.model.MemberOfCongress
import org.climatechangemakers.act.feature.findlegislator.util.suspendTest
import org.climatechangemakers.act.feature.issue.manager.FakeIssueManager
import org.climatechangemakers.act.feature.issue.manager.IssueManager
import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class NetworkCommunicateWithCongressManagerTest {

  @Test fun `sendEmails logs emails for every bioguide contacted`() = suspendTest {
    val request = SendEmailRequest(
      originatingEmailAddress = "k@c.com",
      title = Prefix.Dr,
      firstName = "Foo",
      lastName = "McBar",
      streetAddress = "123 Main Street",
      city = "Richmond",
      state = RepresentedArea.Virginia,
      postalCode = "23223",
      relatedTopics = listOf(Topic.Energy),
      emailBody = "Body",
      emailSubject = "subject",
      relatedIssueId = 1,
      contactedBioguideIds = listOf("1", "2", "3"),
    )

    val fakeActionTrackerManager = FakeActionTrackerManager()
    val fakeMocManager = FakeMemberOfCongressManager().apply {
      request.contactedBioguideIds.forEach { id ->
        memberQueue.send(
          FakeMemberOfCongressManager.DEFAULT_MEMBER.copy(bioguideId = id)
        )
      }
    }

    val fakeIssueManager = FakeIssueManager().apply {
      repeat(3) { titles.send("issue title") }
    }

    val result = manager(
      memberOfCongressManager = fakeMocManager,
      issueManager = fakeIssueManager,
      actionTrackerManager = fakeActionTrackerManager,
    ).sendEmails(request)

    assertTrue(result is Success)
    request.contactedBioguideIds.forEach { id ->
      fakeActionTrackerManager.assertActionEntryMatches(request.originatingEmailAddress, id, request.relatedIssueId)
    }
  }

  @Test fun `sendEmails makes request to CWC for every bioguide with an office code`() = suspendTest {
    val request = SendEmailRequest(
      originatingEmailAddress = "k@c.com",
      title = Prefix.Dr,
      firstName = "Foo",
      lastName = "McBar",
      streetAddress = "123 Main Street",
      city = "Richmond",
      state = RepresentedArea.Virginia,
      postalCode = "23223",
      relatedTopics = listOf(Topic.Energy),
      emailBody = "Body",
      emailSubject = "subject",
      relatedIssueId = 1,
      contactedBioguideIds = listOf("1", "2", "3"),
    )

    val fakeIssueManager = FakeIssueManager()
    val fakeMemberOfCongressManager = FakeMemberOfCongressManager()
    val fakeSenateService = FakeCommunicateWithCongressService { Response.success(Unit) }
    val fakeHouseService = FakeCommunicateWithCongressService { Response.success(Unit) }

    repeat(3) { iteration ->
      fakeIssueManager.titles.send("issue title")
      fakeMemberOfCongressManager.memberQueue.send(
        FakeMemberOfCongressManager.DEFAULT_MEMBER.copy(
          legislativeRole = if (iteration == 1) LegislatorRole.Senator else LegislatorRole.Representative,
          cwcOfficeCode = "this-office-${iteration + 1}"
        )
      )
    }

    val result = manager(
      memberOfCongressManager = fakeMemberOfCongressManager,
      issueManager = fakeIssueManager,
      senateService = fakeSenateService,
      houseService = fakeHouseService,
    ).sendEmails(request)
    assertTrue(result is Success)

    request.contactedBioguideIds.forEach { id ->
      val service = if (id == "2") fakeSenateService else fakeHouseService
      val body = service.capturedBodies.tryReceive().getOrThrow()
      assertEquals("this-office-$id", body.recipient.officeCode)
    }
  }

  @Test fun `sendEmails does not make request to CWC for bioguides without an office code`() = suspendTest {
    val request = SendEmailRequest(
      originatingEmailAddress = "k@c.com",
      title = Prefix.Dr,
      firstName = "Foo",
      lastName = "McBar",
      streetAddress = "123 Main Street",
      city = "Richmond",
      state = RepresentedArea.Virginia,
      postalCode = "23223",
      relatedTopics = listOf(Topic.Energy),
      emailBody = "Body",
      emailSubject = "subject",
      relatedIssueId = 1,
      contactedBioguideIds = listOf("1", "2", "3"),
    )

    val fakeIssueManager = FakeIssueManager()
    val fakeMemberOfCongressManager = FakeMemberOfCongressManager()
    val fakeSenateService = FakeCommunicateWithCongressService { Response.success(Unit) }
    val fakeHouseService = FakeCommunicateWithCongressService { Response.success(Unit) }

    repeat(3) { iteration ->
      fakeIssueManager.titles.send("issue title")
      fakeMemberOfCongressManager.memberQueue.send(
        FakeMemberOfCongressManager.DEFAULT_MEMBER.copy(
          legislativeRole = if (iteration == 1) LegislatorRole.Senator else LegislatorRole.Representative,
          cwcOfficeCode = null,
        )
      )
    }

    val result = manager(
      memberOfCongressManager = fakeMemberOfCongressManager,
      issueManager = fakeIssueManager,
      senateService = fakeSenateService,
      houseService = fakeHouseService,
    ).sendEmails(request)

    assertTrue(result is Success)
    assertTrue(fakeSenateService.capturedBodies.isEmpty)
    assertTrue(fakeHouseService.capturedBodies.isEmpty)
  }

  @Test fun `sendEmails associates with the correct campaignId`() = suspendTest {
    val request = SendEmailRequest(
      originatingEmailAddress = "k@c.com",
      title = Prefix.Dr,
      firstName = "Foo",
      lastName = "McBar",
      streetAddress = "123 Main Street",
      city = "Richmond",
      state = RepresentedArea.Virginia,
      postalCode = "23223",
      relatedTopics = listOf(Topic.Energy),
      emailBody = "Body",
      emailSubject = "subject",
      relatedIssueId = 1,
      contactedBioguideIds = listOf("1", "2", "3"),
    )

    val fakeIssueManager = FakeIssueManager()
    val fakeMemberOfCongressManager = FakeMemberOfCongressManager()
    val fakeSenateService = FakeCommunicateWithCongressService { Response.success(Unit) }
    val fakeHouseService = FakeCommunicateWithCongressService { Response.success(Unit) }

    repeat(3) { iteration ->
      fakeIssueManager.titles.send("issue title")
      fakeMemberOfCongressManager.memberQueue.send(
        FakeMemberOfCongressManager.DEFAULT_MEMBER.copy(
          legislativeRole = if (iteration == 1) LegislatorRole.Senator else LegislatorRole.Representative,
        )
      )
    }

    val result = manager(
      memberOfCongressManager = fakeMemberOfCongressManager,
      issueManager = fakeIssueManager,
      senateService = fakeSenateService,
      houseService = fakeHouseService,
    ).sendEmails(request)
    assertTrue(result is Success)

    request.contactedBioguideIds.forEach { id ->
      val service = if (id == "2") fakeSenateService else fakeHouseService
      val body = service.capturedBodies.tryReceive().getOrThrow()
      assertEquals("cf9133df63e2b5eb9a567c3e7b4f1a0f4688719d33730833dee1ea591047c293", body.delivery.campaignId)
    }
  }

  @Test fun `failed request is returned from sendEmails`() = suspendTest {
    val request = SendEmailRequest(
      originatingEmailAddress = "k@c.com",
      title = Prefix.Dr,
      firstName = "Foo",
      lastName = "McBar",
      streetAddress = "123 Main Street",
      city = "Richmond",
      state = RepresentedArea.Virginia,
      postalCode = "23223",
      relatedTopics = listOf(Topic.Energy),
      emailBody = "Body",
      emailSubject = "subject",
      relatedIssueId = 1,
      contactedBioguideIds = listOf("1", "2", "3"),
    )

    val fakeIssueManager = FakeIssueManager()
    val fakeMemberOfCongressManager = FakeMemberOfCongressManager()
    val fakeSenteService = FakeCommunicateWithCongressService { error("foo") }
    val fakeHouseService = FakeCommunicateWithCongressService { error("blah") }

    repeat(3) {
      fakeIssueManager.titles.send("issue title")
      fakeMemberOfCongressManager.memberQueue.send(
        FakeMemberOfCongressManager.DEFAULT_MEMBER,
      )
    }

    val failed = manager(
      memberOfCongressManager = fakeMemberOfCongressManager,
      issueManager = fakeIssueManager,
      senateService = fakeSenteService,
      houseService = fakeHouseService,
    ).sendEmails(request)
    assertTrue(failed is Failure)
  }

  @Test fun `email failure returns only failed bioguide ids`() = suspendTest {
    val request = SendEmailRequest(
      originatingEmailAddress = "k@c.com",
      title = Prefix.Dr,
      firstName = "Foo",
      lastName = "McBar",
      streetAddress = "123 Main Street",
      city = "Richmond",
      state = RepresentedArea.Virginia,
      postalCode = "23223",
      relatedTopics = listOf(Topic.Energy),
      emailBody = "Body",
      emailSubject = "subject",
      relatedIssueId = 1,
      contactedBioguideIds = listOf("1", "2", "3"),
    )

    val fakeIssueManager = FakeIssueManager()
    val fakeMemberOfCongressManager = FakeMemberOfCongressManager()
    val fakeHouseService = FakeCommunicateWithCongressService { error("blah") }

    repeat(3) { iteration ->
      fakeIssueManager.titles.send("issue title")
      fakeMemberOfCongressManager.memberQueue.send(
        FakeMemberOfCongressManager.DEFAULT_MEMBER.copy(
          legislativeRole = if (iteration == 2) LegislatorRole.Representative else LegislatorRole.Senator,
        )
      )
    }

    val failed = manager(
      memberOfCongressManager = fakeMemberOfCongressManager,
      issueManager = fakeIssueManager,
      houseService = fakeHouseService,
    ).sendEmails(request)
    assertTrue(failed is Failure)
    assertEquals(listOf("3"), failed.errorData)
  }

  @Test fun `manager selects bills for related issue id`() = suspendTest {
    val request = SendEmailRequest(
      originatingEmailAddress = "k@c.com",
      title = Prefix.Dr,
      firstName = "Foo",
      lastName = "McBar",
      streetAddress = "123 Main Street",
      city = "Richmond",
      state = RepresentedArea.Virginia,
      postalCode = "23223",
      relatedTopics = listOf(Topic.Energy),
      emailBody = "Body",
      emailSubject = "subject",
      relatedIssueId = 1,
      contactedBioguideIds = listOf("1"),
    )

    val fakeIssueManager = FakeIssueManager().apply {
      titles.send("issue title")
    }
    val fakeMemberOfCongressManager = FakeMemberOfCongressManager().apply {
      memberQueue.send(FakeMemberOfCongressManager.DEFAULT_MEMBER)
    }
    val billManager = FakeBillManager { id ->
      if (id == 1L) emptyList() else throw IllegalArgumentException("Wrong ID.")
    }

    val result = manager(
      memberOfCongressManager = fakeMemberOfCongressManager,
      issueManager = fakeIssueManager,
      billManager = billManager,
    ).sendEmails(request)
    assertTrue(result is Success)
  }

  private fun manager(
    senateService: SenateCommunicateWithCongressService = FakeCommunicateWithCongressService { Response.success(Unit) },
    houseService: HouseCommunicateWithCongressService = FakeCommunicateWithCongressService { Response.success(Unit) },
    memberOfCongressManager: MemberOfCongressManager = FakeMemberOfCongressManager(),
    actionTrackerManager: ActionTrackerManager = FakeActionTrackerManager(),
    billManager: BillManager = FakeBillManager{ emptyList() },
    issueManager: IssueManager = FakeIssueManager(),
    logger: Logger = LoggerFactory.getLogger(this::class.java),
  ) = NetworkCommunicateWithCongressManager(
    senateService = senateService,
    houseService = houseService,
    memberOfCongressManager = memberOfCongressManager,
    actionTrackerManager = actionTrackerManager,
    billManager = billManager,
    issueManager = issueManager,
    logger = logger,
  )
}

private suspend fun FakeActionTrackerManager.assertActionEntryMatches(
  email: String,
  bioguide: String,
  issueId: Long,
) {
  assertEquals(email, capturedEmail.receive())
  assertEquals(bioguide, capturedBioguideId.receive())
  assertEquals(issueId, capturedIssueId.receive())
  assertNotNull(capturedEmailDeliveryIds.tryReceive().getOrNull())
}
