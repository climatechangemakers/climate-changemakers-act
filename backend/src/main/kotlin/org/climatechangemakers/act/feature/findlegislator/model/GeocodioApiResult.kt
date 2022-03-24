package org.climatechangemakers.act.feature.findlegislator.model

import kotlinx.serialization.Serializable

@Serializable class GeocodioApiResult(
  val results: List<GeocodeResult> = emptyList()
)

@Serializable class GeocodeResult(
  val location: Location,
)