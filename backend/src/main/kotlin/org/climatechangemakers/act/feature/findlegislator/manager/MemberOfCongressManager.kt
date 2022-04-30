package org.climatechangemakers.act.feature.findlegislator.manager

import org.climatechangemakers.act.common.model.RepresentedArea
import org.climatechangemakers.act.feature.findlegislator.model.MemberOfCongress

interface MemberOfCongressManager {

  suspend fun getMemberOfCongressForBioguide(
    bioguideId: String
  ): MemberOfCongress

  suspend fun getMembersForCongressionalDistrict(
    state: RepresentedArea,
    district: Short,
  ): List<MemberOfCongress>

  suspend fun getTwitterHandlesForBioguides(
    bioguides: List<String>,
  ): List<String>
}