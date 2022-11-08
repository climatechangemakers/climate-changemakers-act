package org.climatechangemakers.act.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.respond
import io.ktor.util.logging.error
import kotlinx.serialization.SerializationException
import org.climatechangemakers.act.common.extension.state
import org.postgresql.util.PSQLException
import org.postgresql.util.PSQLState
import retrofit2.HttpException

fun Application.configureExceptionHandler() {
  install(StatusPages) {

    val log = this@configureExceptionHandler.log

    exception<SerializationException> { call, cause ->
      cause.message?.let(log::error)
      call.respond(HttpStatusCode.BadRequest, cause.message ?: "")
    }

    exception<HttpException> { call, cause ->
      log.error(cause)
      cause.response()?.errorBody()?.string()?.let(log::error)
      call.respond(HttpStatusCode.InternalServerError, cause.message())
    }

    exception<PSQLException> { call, cause ->
      cause.message?.let(log::error)

      val responseCode = when (cause.state) {
        PSQLState.FOREIGN_KEY_VIOLATION -> HttpStatusCode.NotFound
        PSQLState.UNIQUE_VIOLATION -> HttpStatusCode.BadRequest
        else -> HttpStatusCode.InternalServerError
      }

      call.respond(responseCode, cause.message ?: "")
    }

    exception<NoSuchElementException> { call, cause ->
      call.respond(HttpStatusCode.NotFound, cause.message ?: "")
    }

    exception<Exception> { call, cause ->
      cause.message?.let(log::error)
      call.respond(HttpStatusCode.InternalServerError, cause.message ?: "")
    }
  }
}
