package com.climatechangemakers.act.feature.findlegislator.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable class GoogleCivicInformationResponse(
  val offices: List<GoogleCivicOffice>,
  @SerialName("officials") val legislators: List<GoogleCivicLegislator>,
)

@Serializable class GoogleCivicLegislator(
  val name: String,
  @SerialName("phones") val phoneNumbers: List<String>,
  val urls: List<String>,
  val photoUrl: String? = null,
)

@Serializable class GoogleCivicOffice(
  @SerialName("name") val role: LegislatorRole,
  @SerialName("officialIndices") val legislatorIndices: List<Int>,
)
