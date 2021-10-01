package org.climatechangemakers.act.common.extension

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond

suspend fun ApplicationCall.respondNothing() = respond(status = HttpStatusCode.NoContent, message = Unit)