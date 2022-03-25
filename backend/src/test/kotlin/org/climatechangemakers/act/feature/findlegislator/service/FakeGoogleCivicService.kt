package org.climatechangemakers.act.feature.findlegislator.service

import kotlinx.coroutines.channels.Channel
import org.climatechangemakers.act.common.model.RepresentedArea
import org.climatechangemakers.act.feature.findlegislator.model.GoogleCivicApiResult

class FakeGoogleCivicService : GoogleCivicService {

  val resultQueue = Channel<GoogleCivicApiResult>(Channel.BUFFERED)

  override suspend fun getCongressionalDistrict(
    address: String,
    levels: String,
    roles: String,
    includeOffices: Boolean,
  ): GoogleCivicApiResult = resultQueue.tryReceive().getOrThrow()

  companion object {

    fun buildApiResponse(
      state: RepresentedArea,
      district: Short,
    ): GoogleCivicApiResult = GoogleCivicApiResult(
      mapOf("ocd-division/country:us/state:${state.value}/cd:$district" to emptyMap())
    )
  }
}