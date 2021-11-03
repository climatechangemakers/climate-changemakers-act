package org.climatechangemakers.act.feature.membership.model

import kotlinx.serialization.Serializable

@Serializable class CheckMembershipResponse(
  val isMember: Boolean,
)