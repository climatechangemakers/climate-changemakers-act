package org.climatechangemakers.act.plugins

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import kotlinx.serialization.SerializationException
import org.postgresql.util.PSQLException
import retrofit2.HttpException

fun Application.configureExceptionHandler() {
  install(StatusPages) {

    exception<SerializationException> { cause ->
      call.respond(HttpStatusCode.BadRequest, cause.message ?: "")
    }

    exception<HttpException> { cause ->
      // TODO(kcianfarini) log this
      call.respond(HttpStatusCode.InternalServerError, cause.message())
    }

    exception<Exception> { cause ->
      call.respond(HttpStatusCode.InternalServerError, cause.message ?: "")
    }
  }
}
