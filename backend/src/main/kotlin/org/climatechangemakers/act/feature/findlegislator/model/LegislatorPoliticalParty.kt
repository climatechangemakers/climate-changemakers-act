package org.climatechangemakers.act.feature.findlegislator.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.climatechangemakers.act.common.serializers.StringEnum

@Serializable enum class LegislatorPoliticalParty(override val value: String) : StringEnum {
  @SerialName("republican") Republican("Republican"),
  @SerialName("democrat") Democrat("Democrat"),
  @SerialName("independent") Independent("Independent"),
  @SerialName("libertarian") Libertarian("Libertarian"),
}