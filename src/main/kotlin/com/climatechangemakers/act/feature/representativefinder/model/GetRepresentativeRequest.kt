package com.climatechangemakers.act.feature.representativefinder.model

import kotlinx.serialization.Serializable

@Serializable class GetRepresentativeRequest(
  val streetAddress: String,
  val city: String,
  val state: String,
  val postalCode: String,
)