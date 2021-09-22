package org.climatechangemakers.act.feature.findlegislator.service

import org.climatechangemakers.act.feature.findlegislator.model.GeocodioApiResult

class FakeGeocodioService(
  private val fakeResultProvider: () -> GeocodioApiResult,
) : GeocodioService {

  var capturedQuery: String? = null

  override suspend fun geocode(query: String, fields: List<String>): GeocodioApiResult{
    capturedQuery = query
    return fakeResultProvider()
  }
}