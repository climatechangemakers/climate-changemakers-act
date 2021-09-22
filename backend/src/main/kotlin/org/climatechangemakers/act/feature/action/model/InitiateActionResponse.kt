package org.climatechangemakers.act.feature.action.model

import org.climatechangemakers.act.feature.findlegislator.model.Legislator
import kotlinx.serialization.Serializable

@Serializable class InitiateActionResponse(
  val initiatorEmail: String,
  val legislators: List<Legislator>,
)