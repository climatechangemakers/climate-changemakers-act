package org.climatechangemakers.act

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.climatechangemakers.act.di.DaggerApiComponent
import org.climatechangemakers.act.plugins.configureRouting
import org.climatechangemakers.act.plugins.configureSerialization
import org.climatechangemakers.act.plugins.configureExceptionHandler

fun main() {
  val apiComponent = DaggerApiComponent.create()

  embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
    configureExceptionHandler()
    configureSerialization(apiComponent.json())
    configureRouting(apiComponent)
  }.start(wait = true)
}
