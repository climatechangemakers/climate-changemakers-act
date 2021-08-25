package com.climatechangemakers.act.feature.findlegislator.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable enum class LegislatorPoliticalParty {
  @SerialName("republican") Republican,
  @SerialName("democrat") Democrat,
  @SerialName("independent") Independent,
  @SerialName("libertarian") Libertarian,
}