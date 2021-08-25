package com.climatechangemakers.act.feature.findlegislator.model

import kotlinx.serialization.Serializable

@Serializable data class LegislatorArea(
  val state: String,
  val districtNumber: Int?,
)
