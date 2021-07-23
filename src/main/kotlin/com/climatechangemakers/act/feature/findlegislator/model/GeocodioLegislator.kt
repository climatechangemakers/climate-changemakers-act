package com.climatechangemakers.act.feature.findlegislator.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable class GeocodioLegislator(
  val type: LegislatorType,
  val bio: LegislatorBio,
  @SerialName("contact") val contactInfo: LegislatorContactInformation,
)

@Serializable class LegislatorBio(
  @SerialName("last_name") val lastName: String,
  @SerialName("first_name") val firstName: String,
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
