package org.climatechangemakers.act.feature.cms.routing

import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route

fun Route.cmsAuthenticated(build: Route.() -> Unit) = authenticate(
  "cms-basic-auth",
  build = build,
)