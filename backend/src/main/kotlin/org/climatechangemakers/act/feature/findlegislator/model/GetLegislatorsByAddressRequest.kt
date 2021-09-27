package org.climatechangemakers.act.feature.findlegislator.model

import kotlinx.serialization.Serializable
import org.climatechangemakers.act.common.model.RepresentedArea

@Serializable class GetLegislatorsByAddressRequest(
  val streetAddress: String,
  val city: String,
  val state: RepresentedArea,
  val postalCode: String,
)