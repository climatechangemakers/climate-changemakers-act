package org.climatechangemakers.act.feature.findlegislator.manager

import org.climatechangemakers.act.common.model.RepresentedArea
import org.climatechangemakers.act.feature.findlegislator.model.LegislatorPoliticalParty
import org.climatechangemakers.act.feature.findlegislator.model.LegislatorRole
import org.climatechangemakers.act.feature.findlegislator.model.MemberOfCongress
import org.climatechangemakers.act.feature.findlegislator.util.suspendTest
import org.climatechangemakers.act.feature.util.TestContainerProvider
import org.climatechangemakers.act.feature.util.insertMemberOfCongress
import org.junit.Test
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.assertEquals

class DatabaseMemberOfCongressManagerTest : TestContainerProvider() {

  private val manager = DatabaseMemberOfCongressManager(database, EmptyCoroutineContext)

  @Test fun `select for congressional district get house representative`() = suspendTest {
    val expectedMember = MemberOfCongress(
      "b",
      "b",
      LegislatorRole.Representative,
      RepresentedArea.Virginia,
      1,
      LegislatorPoliticalParty.Republican,
      "555-555-5555",
      "twitter2",
      "barfoo",
    )

    driver.insertMemberOfCongress(expectedMember)
    assertEquals(
      listOf(expectedMember),
      manager.getMembersForCongressionalDistrict(RepresentedArea.Virginia, 1),
    )
  }

  @Test fun `select for congressional district gets senators`() = suspendTest {
    val expectedMember = MemberOfCongress(
      "b",
      "b",
      LegislatorRole.Senator,
      RepresentedArea.Virginia,
      null,
      LegislatorPoliticalParty.Republican,
      "555-555-5555",
      "twitter2",
      "barfoo",
    )

    driver.insertMemberOfCongress(expectedMember)
    assertEquals(
      listOf(expectedMember),
      manager.getMembersForCongressionalDistrict(RepresentedArea.Virginia, 1),
    )
  }

  @Test fun `select for congressional district omits irrelevant content`() = suspendTest {

  }

  @Test fun `select twitter handles omits null values`() = suspendTest {
    val expectedMember = MemberOfCongress(
      "b",
      "b",
      LegislatorRole.Senator,
      RepresentedArea.Virginia,
      null,
      LegislatorPoliticalParty.Republican,
      "555-555-5555",
      "twitter2",
      "barfoo",
    )

    driver.insertMemberOfCongress(expectedMember)
    driver.insertMemberOfCongress(expectedMember.copy(bioguideId = "a", twitterHandle = null))
    assertEquals(
      expected = listOf("twitter2"),
      actual = manager.getTwitterHandlesForBioguides(listOf("a", "b"))
    )
  }
}