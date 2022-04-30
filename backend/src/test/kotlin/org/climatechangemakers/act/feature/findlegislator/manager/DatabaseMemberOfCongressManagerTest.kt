package org.climatechangemakers.act.feature.findlegislator.manager

import org.climatechangemakers.act.common.model.RepresentedArea
import org.climatechangemakers.act.feature.findlegislator.model.LegislatorPoliticalParty
import org.climatechangemakers.act.feature.findlegislator.model.LegislatorRole
import org.climatechangemakers.act.feature.findlegislator.model.MemberOfCongress
import org.climatechangemakers.act.feature.findlegislator.util.suspendTest
import org.climatechangemakers.act.feature.util.TestContainerProvider
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

    insert(expectedMember)
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

    insert(expectedMember)
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

    insert(expectedMember)
    insert(expectedMember.copy(bioguideId = "a", twitterHandle = null))
    assertEquals(
      expected = listOf("twitter2"),
      actual = manager.getTwitterHandlesForBioguides(listOf("a", "b"))
    )
  }

  private fun insert(member: MemberOfCongress) = driver.execute(
    0, "INSERT INTO member_of_congress VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)", 9
  ) {
    bindString(1, member.bioguideId)
    bindString(2, member.fullName)
    bindString(3, member.legislativeRole.value)
    bindString(4, member.representedArea.value)
    bindLong(5, member.congressionalDistrict?.toLong())
    bindString(6, member.party.value)
    bindString(7, member.dcPhoneNumber)
    bindString(8, member.twitterHandle)
    bindString(9, member.cwcOfficeCode)
  }
}