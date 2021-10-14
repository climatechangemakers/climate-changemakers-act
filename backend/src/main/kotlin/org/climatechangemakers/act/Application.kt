package org.climatechangemakers.act

import io.ktor.application.log
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.climatechangemakers.act.di.DaggerApiComponent
import org.climatechangemakers.act.di.LoggerModule
import org.climatechangemakers.act.plugins.configureRouting
import org.climatechangemakers.act.plugins.configureSerialization
import org.climatechangemakers.act.plugins.configureExceptionHandler

fun main() {
  embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
    val apiComponent = DaggerApiComponent.builder()
      .loggerModule(LoggerModule(log))
      .build()

    configureExceptionHandler()
    configureSerialization(apiComponent.json())
    configureRouting(apiComponent)
  }.start(wait = true)
}
