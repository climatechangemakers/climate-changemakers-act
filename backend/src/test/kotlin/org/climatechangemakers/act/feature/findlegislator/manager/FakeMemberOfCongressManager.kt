package org.climatechangemakers.act.feature.findlegislator.manager

import kotlinx.coroutines.channels.Channel
import org.climatechangemakers.act.common.model.RepresentedArea
import org.climatechangemakers.act.feature.findlegislator.model.LegislatorPoliticalParty
import org.climatechangemakers.act.feature.findlegislator.model.LegislatorRole
import org.climatechangemakers.act.feature.findlegislator.model.MemberOfCongress

class FakeMemberOfCongressManager : MemberOfCongressManager {

  val memberQueue: Channel<MemberOfCongress> = Channel(Channel.BUFFERED)
  val memberListQueue: Channel<List<MemberOfCongress>> = Channel(Channel.BUFFERED)
  val twitterHandlesQueue: Channel<List<String>> = Channel(Channel.BUFFERED)

  override suspend fun getMemberOfCongressForBioguide(bioguideId: String): MemberOfCongress {
    return memberQueue.tryReceive().getOrThrow()
  }

  override suspend fun getMembersForCongressionalDistrict(
    state: RepresentedArea,
    district: Short
  ): List<MemberOfCongress> {
    return memberListQueue.tryReceive().getOrThrow()
  }

  override suspend fun getTwitterHandlesForBioguides(bioguides: List<String>): List<String> {
    return twitterHandlesQueue.tryReceive().getOrThrow()
  }

  companion object {
    val DEFAULT_MEMBER = MemberOfCongress(
      bioguideId = "bioguide",
      fullName = "Full name",
      legislativeRole = LegislatorRole.Representative,
      representedArea = RepresentedArea.Virginia,
      congressionalDistrict = 1,
      party = LegislatorPoliticalParty.Republican,
      dcPhoneNumber = "(555) 555-5555",
      twitterHandle = "foo",
      cwcOfficeCode = "code",
    )
  }
}