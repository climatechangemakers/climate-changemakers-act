package org.climatechangemakers.act.feature.findlegislator.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.climatechangemakers.act.common.serializers.StringEnum

@Serializable enum class LegislatorRole(override val value: String) : StringEnum {
  @SerialName("senator") Senator("sen"),
  @SerialName("representative") Representative("rep"),
}
