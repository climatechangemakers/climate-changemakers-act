package org.climatechangemakers.act

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.climatechangemakers.act.plugins.configureRouting
import org.climatechangemakers.act.plugins.configureSerialization
import org.climatechangemakers.act.plugins.configureExceptionHandler

fun main() {
  embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
    configureExceptionHandler()
    configureSerialization()
    configureRouting()
  }.start(wait = true)
}
