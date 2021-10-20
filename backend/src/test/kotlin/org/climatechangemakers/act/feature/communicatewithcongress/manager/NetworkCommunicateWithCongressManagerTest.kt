package org.climatechangemakers.act.feature.communicatewithcongress.manager

import okhttp3.MediaType
import okhttp3.ResponseBody
import org.climatechangemakers.act.common.model.Failure
import org.climatechangemakers.act.common.model.RepresentedArea
import org.climatechangemakers.act.common.model.Success
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
import org.slf4j.LoggerFactory
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NetworkCommunicateWithCongressManagerTest {

  private val fakeMemberOfCongressManager = MemberOfCongressManager { bioguideId ->
    MemberOfCongress(
      bioguideId = bioguideId,
      fullName = "Frank McDoodle",
      legislativeRole = if (bioguideId == "2") LegislatorRole.Senator else LegislatorRole.Representative,
      representedArea = RepresentedArea.WestVirginia,
      congressionalDistrict = null,
      party = LegislatorPoliticalParty.Republican,
      dcPhoneNumber = "867-5309",
      twitterHandle = "@somedude",
      cwcOfficeCode = if (bioguideId == "1") null else "this-office-$bioguideId",
    )
  }

  private val fakeActionManager = FakeActionTrackerManager()
  private val fakeSenteService = FakeCommunicateWithCongressService { Response.success(Unit) }
  private val fakeHouseService = FakeCommunicateWithCongressService { Response.success(Unit) }
  private val manager = NetworkCommunicateWithCongressManager(
    senateService = fakeSenteService,
    houseService = fakeHouseService,
    memberOfCongressManager = fakeMemberOfCongressManager,
    actionTrackerManager = fakeActionManager,
    LoggerFactory.getLogger(this::class.java),
  )


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

    val result = manager.sendEmails(request)
    assertTrue(result is Success)
    request.contactedBioguideIds.drop(1).forEach { id ->
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

    val result = manager.sendEmails(request)
    assertTrue(result is Success)

    request.contactedBioguideIds.drop(1).forEach { id ->
      val service = if (id == "2") fakeSenteService else fakeHouseService
      assertEquals("this-office-$id", service.capturedBodies.tryReceive().getOrThrow().recipient.officeCode)
    }
  }

  @Test fun `failed request is returned from sendEmails`() = suspendTest {
    val error = Response.error<Unit>(500, ResponseBody.create(MediaType.get("application/json"), "error"))
    val fakeSenteService = FakeCommunicateWithCongressService { error }
    val fakeHouseService = FakeCommunicateWithCongressService { error }
    val manager = NetworkCommunicateWithCongressManager(
      senateService = fakeSenteService,
      houseService = fakeHouseService,
      memberOfCongressManager = fakeMemberOfCongressManager,
      actionTrackerManager = fakeActionManager,
      LoggerFactory.getLogger(this::class.java),
    )
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

    val failed = manager.sendEmails(request)
    assertTrue(failed is Failure)
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