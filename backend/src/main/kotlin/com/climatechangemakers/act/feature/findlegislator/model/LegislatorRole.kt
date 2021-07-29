package com.climatechangemakers.act.feature.findlegislator.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable enum class LegislatorRole {
  @SerialName("senator") Senator,
  @SerialName("representative") Representative,
}
