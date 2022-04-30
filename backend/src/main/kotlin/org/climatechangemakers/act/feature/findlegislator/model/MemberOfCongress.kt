package org.climatechangemakers.act.feature.findlegislator.model

import org.climatechangemakers.act.common.model.RepresentedArea

data class MemberOfCongress(
  val bioguideId: String,
  val fullName: String,
  val legislativeRole: LegislatorRole,
  val representedArea: RepresentedArea,
  val congressionalDistrict: Short?,
  val party: LegislatorPoliticalParty,
  val dcPhoneNumber: String,
  val twitterHandle: String?,
  val cwcOfficeCode: String?,
)