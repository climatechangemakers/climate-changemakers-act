package com.climatechangemakers.act.feature.findlegislator.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@Serializable class Location(private val lat: Double, @SerialName("lng") private val long: Double) {

  fun distanceBetween(other: Location): Double {
    val r = 6371 // Earth's radius in km
    val deltaLat = this.lat.radian - other.lat.radian
    val deltaLong = this.long.radian - other.long.radian
    val a = sin(deltaLat / 2).pow(2) + cos(this.lat.radian) * cos(other.lat.radian) * sin(deltaLong / 2).pow(2)
    val c = 2 * asin(sqrt(a))
    return c * r
  }
}

private val Double.radian get() = this * PI / 180