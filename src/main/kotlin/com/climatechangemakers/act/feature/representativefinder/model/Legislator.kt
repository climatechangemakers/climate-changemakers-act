package com.climatechangemakers.act.feature.representativefinder.model

import kotlinx.serialization.Serializable

@Serializable data class Legislator(
  val name: String,
  val type: LegislatorType,
  val siteUrl: String,
  val phone: String,
)