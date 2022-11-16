package org.climatechangemakers.act.feature.cms.routing

import io.ktor.server.application.call
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.climatechangemakers.act.feature.cms.controller.ContentManagementBillController

fun Route.contentManagementApiRoutes(
  billController: ContentManagementBillController,
) = route("/api") {
  route("/bills") {
    post { billController.postBill(call) }
    get { billController.getBills(call) }
  }
}