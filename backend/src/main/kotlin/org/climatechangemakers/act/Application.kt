package org.climatechangemakers.act

import io.ktor.server.application.log
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.climatechangemakers.act.di.DaggerApiComponent
import org.climatechangemakers.act.di.LoggerModule
import org.climatechangemakers.act.feature.cms.plugin.configureContentManagementAuthentication
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
    configureContentManagementAuthentication(apiComponent.userVerificationManager())
  }.start(wait = true)
}
