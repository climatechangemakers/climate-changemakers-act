package com.climatechangemakers.act.feature.representativefinder.service

import com.climatechangemakers.act.feature.representativefinder.model.GeocodioApiResult

class FakeGeocodioService(
  private val fakeResultProvider: () -> GeocodioApiResult,
) : GeocodioService {

  var capturedQuery: String? = null
  var capturedFields: List<String> = emptyList()

  override suspend fun getLegislators(
    query: String,
    fields: List<String>,
  ): GeocodioApiResult {
    capturedQuery = query
    capturedFields = fields
    return fakeResultProvider()
  }
}