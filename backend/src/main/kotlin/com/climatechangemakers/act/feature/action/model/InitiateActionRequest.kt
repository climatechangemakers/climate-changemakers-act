package com.climatechangemakers.act.feature.action.model

import kotlinx.serialization.Serializable

@Serializable class InitiateActionRequest(
  val email: String,
  val streetAddress: String,
  val city: String,
  val state: String,
  val postalCode: String,
)