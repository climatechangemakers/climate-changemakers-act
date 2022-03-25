package org.climatechangemakers.act.feature.action.model

import org.climatechangemakers.act.feature.findlegislator.model.MemberOfCongressDto
import kotlinx.serialization.Serializable

@Serializable class InitiateActionResponse(
  val initiatorEmail: String,
  val legislators: List<MemberOfCongressDto>,
)