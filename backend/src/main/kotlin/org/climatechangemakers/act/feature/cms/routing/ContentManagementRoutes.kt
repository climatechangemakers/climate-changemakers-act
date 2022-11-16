package org.climatechangemakers.act.feature.cms.routing

import io.ktor.server.routing.Route
import io.ktor.server.routing.route
import org.climatechangemakers.act.feature.cms.controller.ContentManagementBillController

fun Route.contentManagementRoutes(
  billController: ContentManagementBillController,
) = cmsAuthenticated {
  route("/cms") {
    contentManagementApiRoutes(billController)
  }
}