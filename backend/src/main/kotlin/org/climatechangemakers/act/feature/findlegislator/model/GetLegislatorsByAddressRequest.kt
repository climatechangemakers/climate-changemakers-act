package org.climatechangemakers.act.feature.findlegislator.model

import kotlinx.serialization.Serializable
import org.climatechangemakers.act.common.model.State

@Serializable class GetLegislatorsByAddressRequest(
  val streetAddress: String,
  val city: String,
  val state: State,
  val postalCode: String,
)