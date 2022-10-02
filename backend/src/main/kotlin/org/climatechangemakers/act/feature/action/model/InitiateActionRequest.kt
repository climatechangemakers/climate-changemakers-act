package org.climatechangemakers.act.feature.action.model

import kotlinx.serialization.Serializable
import org.climatechangemakers.act.common.model.RepresentedArea

@Serializable class InitiateActionRequest(
  val email: String,
  val streetAddress: String,
  val city: String,
  val state: RepresentedArea,
  val postalCode: String,
  val consentToTrackImpact: Boolean,
)