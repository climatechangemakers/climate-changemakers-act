package com.climatechangemakers.act.feature.representativefinder.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable class Legislator(
  val type: LegislatorType,
  @SerialName("contact") val contactInfo: LegislatorContactInformation,
)

@Serializable class LegislatorContactInformation(
  @SerialName("url") val siteUrl: String,
  @SerialName("address") val formattedAddress: String,
  val phone: String,
)

@Serializable enum class LegislatorType {
  @SerialName("representative") Representative,
  @SerialName("senator") Senator,
}
