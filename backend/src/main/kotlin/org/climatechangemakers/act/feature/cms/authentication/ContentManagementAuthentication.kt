package org.climatechangemakers.act.feature.cms.authentication

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.basic

fun Application.configureContentManagementAuthentication() = install(Authentication) {
  basic("cms-basic-auth") {
    realm = "Access to the '/cms' path"
    validate { credentials ->
      // TODO(kcianfarini) Do actual validation!
      credentials.name.takeIf { it == "kevin" }?.let(::UserIdPrincipal)
    }
  }
}