package org.climatechangemakers.act.plugins

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import kotlinx.serialization.SerializationException
import org.climatechangemakers.act.common.extension.state
import org.postgresql.util.PSQLException
import org.postgresql.util.PSQLState
import retrofit2.HttpException

fun Application.configureExceptionHandler() {
  install(StatusPages) {

    exception<SerializationException> { cause ->
      cause.message?.let(log::error)
      call.respond(HttpStatusCode.BadRequest, cause.message ?: "")
    }

    exception<HttpException> { cause ->
      cause.message?.let(log::error)
      call.respond(HttpStatusCode.InternalServerError, cause.message())
    }

    exception<PSQLException> { cause ->
      val responseCode = when (cause.state) {
        PSQLState.FOREIGN_KEY_VIOLATION -> HttpStatusCode.NotFound
        else -> HttpStatusCode.InternalServerError
      }

      call.respond(responseCode, cause.message ?: "")
    }

    exception<NoSuchElementException> { cause ->
      call.respond(HttpStatusCode.NotFound, cause.message ?: "")
    }

    exception<Exception> { cause ->
      cause.message?.let(log::error)
      call.respond(HttpStatusCode.InternalServerError, cause.message ?: "")
    }
  }
}
