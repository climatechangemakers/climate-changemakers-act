package org.climatechangemakers.act.feature.findlegislator.model

import kotlinx.serialization.Serializable

@Serializable class GetLegislatorsByAddressRequest(
  val streetAddress: String,
  val city: String,
  val state: String,
  val postalCode: String,
)