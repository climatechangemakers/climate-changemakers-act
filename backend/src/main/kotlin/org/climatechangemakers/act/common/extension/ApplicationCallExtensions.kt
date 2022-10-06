package org.climatechangemakers.act.common.extension

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.application.ApplicationCall

suspend fun ApplicationCall.respondNothing() = respond(status = HttpStatusCode.NoContent, message = Unit)