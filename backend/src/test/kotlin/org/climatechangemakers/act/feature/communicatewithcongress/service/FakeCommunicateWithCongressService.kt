package org.climatechangemakers.act.feature.communicatewithcongress.service

import kotlinx.coroutines.channels.Channel
import org.climatechangemakers.act.feature.communicatewithcongress.model.CommunicateWithCogressRequest

class FakeCommunicateWithCongressService : SenateCommunicateWithCongressService, HouseCommunicateWithCongressService {

  val capturedBodies = Channel<CommunicateWithCogressRequest>(Channel.BUFFERED)

  override suspend fun contact(request: CommunicateWithCogressRequest) {
    capturedBodies.trySend(request)
  }
}