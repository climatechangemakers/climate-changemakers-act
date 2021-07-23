package com.climatechangemakers.act.feature.representativefinder.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable class CongressionalDistrict(
  val name: String,
  @SerialName("district_number") val districtNumber: Int,
  @SerialName("current_legislators") val currentLegislators: List<GeocodioLegislator> = emptyList(),
)