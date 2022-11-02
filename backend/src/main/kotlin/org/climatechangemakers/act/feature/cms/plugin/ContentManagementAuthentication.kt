package org.climatechangemakers.act.feature.cms.plugin

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.basic
import org.climatechangemakers.act.feature.cms.manager.UserVerificationManager

fun Application.configureContentManagementAuthentication(
  userManager: UserVerificationManager,
) = install(Authentication) {
  basic("cms-basic-auth") {
    realm = "Access to the '/cms' path"
    validate { credentials ->
      credentials.name.takeIf {
        userManager.verifyLogin(credentials.name, credentials.password)
      }?.let(::UserIdPrincipal)
    }
  }
}