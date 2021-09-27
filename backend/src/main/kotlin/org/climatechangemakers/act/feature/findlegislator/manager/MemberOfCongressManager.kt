package org.climatechangemakers.act.feature.findlegislator.manager

import org.climatechangemakers.act.feature.findlegislator.model.MemberOfCongress

fun interface MemberOfCongressManager {
  suspend fun getMemberOfCongressForBioguide(bioguideId: String): MemberOfCongress
}