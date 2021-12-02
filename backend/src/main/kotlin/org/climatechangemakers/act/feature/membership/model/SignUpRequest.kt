package org.climatechangemakers.act.feature.membership.model

import kotlinx.serialization.Serializable
import org.climatechangemakers.act.common.model.RepresentedArea

@Serializable class SignUpRequest(
  val email: String,
  val firstName: String,
  val lastName: String,
  val postalCode: String,
  val state: RepresentedArea,
  val priorExperience: Boolean,
  val referral: String,
  val actionReason: String,
  val socialVerification: String,
)