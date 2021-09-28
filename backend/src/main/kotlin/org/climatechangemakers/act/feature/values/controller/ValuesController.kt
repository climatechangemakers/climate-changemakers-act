package org.climatechangemakers.act.feature.values.controller

import io.ktor.application.ApplicationCall
import io.ktor.response.respond
import org.climatechangemakers.act.common.model.RepresentedArea
import javax.inject.Inject

class ValuesController @Inject constructor() {

  suspend fun areaValues(call: ApplicationCall) {
    call.respond(RepresentedArea.values())
  }
}