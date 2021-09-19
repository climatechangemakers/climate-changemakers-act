package com.climatechangemakers.act.feature.findlegislator.model

import com.climatechangemakers.act.feature.lcvscore.model.LcvScore
import kotlinx.serialization.Serializable

@Serializable data class Legislator(
  val name: String,
  val role: LegislatorRole,
  val siteUrl: String,
  val phoneNumbers: List<String>,
  val imageUrl: String?,
  val area: LegislatorArea,
  val partyAffiliation: LegislatorPoliticalParty,
  val lcvScores: List<LcvScore>,
)