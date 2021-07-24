package com.climatechangemakers.act.feature.findlegislator.model

import com.climatechangemakers.act.feature.lcvscore.model.LcvScore
import kotlinx.serialization.Serializable

@Serializable data class Legislator(
  val name: String,
  val type: LegislatorType,
  val siteUrl: String,
  val phone: String,
  val lcvScores: List<LcvScore>,
)