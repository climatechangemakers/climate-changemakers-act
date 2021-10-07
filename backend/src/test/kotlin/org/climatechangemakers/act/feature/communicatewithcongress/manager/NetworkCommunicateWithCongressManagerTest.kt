package org.climatechangemakers.act.feature.communicatewithcongress.manager

import org.climatechangemakers.act.common.model.RepresentedArea
import org.climatechangemakers.act.feature.action.manager.FakeActionTrackerManager
import org.climatechangemakers.act.feature.action.model.SendEmailRequest
import org.climatechangemakers.act.feature.communicatewithcongress.model.Prefix
import org.climatechangemakers.act.feature.communicatewithcongress.model.Topic
import org.climatechangemakers.act.feature.communicatewithcongress.service.FakeCommunicateWithCongressService
import org.climatechangemakers.act.feature.findlegislator.manager.MemberOfCongressManager
import org.climatechangemakers.act.feature.findlegislator.model.LegislatorPoliticalParty
import org.climatechangemakers.act.feature.findlegislator.model.LegislatorRole
import org.climatechangemakers.act.feature.findlegislator.model.MemberOfCongress
import org.climatechangemakers.act.feature.findlegislator.util.suspendTest
import org.junit.Test
import kotlin.test.assertEquals

class NetworkCommunicateWithCongressManagerTest {

  private val fakeMemberOfCongressManager = MemberOfCongressManager { bioguideId ->
    MemberOfCongress(
      bioguideId = bioguideId,
      fullName = "Frank McDoodle",
      legislativeRole = LegislatorRole.Senator,
      representedArea = RepresentedArea.WestVirginia,
      congressionalDistrict = null,
      party = LegislatorPoliticalParty.Republican,
      dcPhoneNumber = "867-5309",
      twitterHandle = "@somedude",
      cwcOfficeCode = if (bioguideId == "1") null else "this-office-$bioguideId",
    )
  }

  private val fakeActionManager = FakeActionTrackerManager()
  private val fakeService = FakeCommunicateWithCongressService()
  private val manager = NetworkCommunicateWithCongressManager(fakeService, fakeMemberOfCongressManager, fakeActionManager)


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

    manager.sendEmails(request)

    request.contactedBioguideIds.forEach { id ->
      assertActionEntryMatches(request.originatingEmailAddress, id, request.relatedIssueId)
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

    manager.sendEmails(request)

    request.contactedBioguideIds.drop(1).forEach { id ->
      assertEquals("this-office-$id", fakeService.capturedBodies.receive().recipient.officeCode)
    }
  }

  private suspend fun assertActionEntryMatches(
    email: String,
    bioguide: String,
    issueId: Long,
  ) = with(fakeActionManager) {
    assertEquals(email, capturedEmail.receive())
    assertEquals(bioguide, capturedBioguideId.receive())
    assertEquals(issueId, capturedIssueId.receive())
  }
}