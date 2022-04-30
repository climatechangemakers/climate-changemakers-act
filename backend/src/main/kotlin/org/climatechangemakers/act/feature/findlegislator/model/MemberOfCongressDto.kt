package org.climatechangemakers.act.feature.findlegislator.model

import org.climatechangemakers.act.feature.lcvscore.model.LcvScore
import kotlinx.serialization.Serializable

@Serializable data class MemberOfCongressDto(
  val name: String,
  val bioguideId: String,
  val role: LegislatorRole,
  val phoneNumbers: List<String>,
  val imageUrl: String?,
  val twitter: String?,
  val area: LegislatorArea,
  val partyAffiliation: LegislatorPoliticalParty,
  val lcvScores: List<LcvScore>,
)