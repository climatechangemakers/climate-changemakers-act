package org.climatechangemakers.act.feature.findlegislator.model

import kotlinx.serialization.Serializable
import org.climatechangemakers.act.common.model.RepresentedArea

@Serializable data class LegislatorArea(
  val state: RepresentedArea,
  val districtNumber: Int?,
)
