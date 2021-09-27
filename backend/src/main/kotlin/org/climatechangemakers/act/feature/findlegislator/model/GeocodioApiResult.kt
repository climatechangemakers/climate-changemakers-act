package org.climatechangemakers.act.feature.findlegislator.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable class GeocodioApiResult(
  val results: List<GeocodeResult> = emptyList()
)

@Serializable class GeocodeResult(
  val location: Location,
  val fields: Fields,
)

@Serializable class Fields(
  @SerialName("congressional_districts") val congressionalDistricts: List<CongressionalDistrict> = emptyList()
)

@Serializable class CongressionalDistrict(
  @SerialName("current_legislators") val currentLegislators: List<GeocodioLegislator> = emptyList(),
)

@Serializable class GeocodioLegislator(
  val references: LegislatorReferences,
)

@Serializable class LegislatorReferences(
  @SerialName("bioguide_id") val bioguide: String,
)