package com.climatechangemakers.act.feature.representativefinder.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable class GeocodioApiResult(
  val results: List<GeocodeResult> = emptyList()
)

@Serializable class GeocodeResult(
  val fields: Fields,
)

@Serializable class Fields(
  @SerialName("congressional_districts") val congressionalDistricts: List<CongressionalDistrict> = emptyList()
)