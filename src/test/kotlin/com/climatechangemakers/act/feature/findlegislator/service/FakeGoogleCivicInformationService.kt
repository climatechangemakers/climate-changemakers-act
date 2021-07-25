package com.climatechangemakers.act.feature.findlegislator.service

import com.climatechangemakers.act.feature.findlegislator.model.GoogleCivicInformationResponse

class FakeGoogleCivicInformationService(
  private val fakeResultProvider: () -> GoogleCivicInformationResponse,
) : GoogleCivicInformationService {

  var capturedQuery: String? = null

  override suspend fun getLegislators(address: String): GoogleCivicInformationResponse {
    capturedQuery = address
    return fakeResultProvider()
  }
}