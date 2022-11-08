package org.climatechangemakers.act.feature.bill.routing

import io.ktor.server.application.call
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.climatechangemakers.act.feature.bill.controller.BillController
import org.climatechangemakers.act.feature.cms.routing.cmsAuthenticated

fun Route.billRoutes(controller: BillController) = route("/bills") {

  cmsAuthenticated {
    post { controller.postBill(call) }
  }
}