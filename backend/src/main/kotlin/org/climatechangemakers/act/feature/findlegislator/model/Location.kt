package org.climatechangemakers.act.feature.findlegislator.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable class Location(val lat: Double, @SerialName("lng") val long: Double)