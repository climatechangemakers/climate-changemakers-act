package com.climatechangemakers.act.feature.action.model

import com.climatechangemakers.act.feature.findlegislator.model.Legislator
import kotlinx.serialization.Serializable

@Serializable class InitiateActionResponse(
  val initiatorEmail: String,
  val legislators: List<Legislator>,
)