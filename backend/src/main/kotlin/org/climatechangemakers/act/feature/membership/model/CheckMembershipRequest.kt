package org.climatechangemakers.act.feature.membership.model

import kotlinx.serialization.Serializable

@Serializable class CheckMembershipRequest(
  val email: String,
)