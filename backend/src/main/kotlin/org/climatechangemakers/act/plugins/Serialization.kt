package org.climatechangemakers.act.plugins

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.serialization.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.serializersModuleOf
import org.climatechangemakers.act.common.model.RepresentedAreaSerializer

fun Application.configureSerialization(json: Json) {
  install(ContentNegotiation) {
    json(json)
  }
}