package com.climatechangemakers.act.feature.representativefinder.model

import kotlinx.serialization.Serializable

@Serializable class GetLegislatorsRequest(
  val streetAddress: String,
  val city: String,
  val state: String,
  val postalCode: String,
)