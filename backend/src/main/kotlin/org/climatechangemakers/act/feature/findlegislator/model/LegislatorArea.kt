package org.climatechangemakers.act.feature.findlegislator.model

import kotlinx.serialization.Serializable
import org.climatechangemakers.act.common.model.State

@Serializable data class LegislatorArea(
  val state: State,
  val districtNumber: Int?,
)
