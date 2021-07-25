package com.climatechangemakers.act.feature.findlegislator.model

import kotlinx.serialization.Serializable

@Serializable class GetLegislatorsRequest(
  val streetAddress: String,
  val city: String,
  val state: String,
  val postalCode: String,
)