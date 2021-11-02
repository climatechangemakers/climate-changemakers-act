package org.climatechangemakers.act.feature.action.model

import org.climatechangemakers.act.common.model.RepresentedArea

class SignUpRequest(
  val email: String,
  val firstName: String,
  val lastName: String,
  val state: RepresentedArea,
  val postalCode: String,
  val priorExperience: Boolean,
)