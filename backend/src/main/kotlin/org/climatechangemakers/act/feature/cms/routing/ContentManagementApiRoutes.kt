package org.climatechangemakers.act.feature.cms.routing

import io.ktor.server.application.call
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import org.climatechangemakers.act.feature.cms.controller.ContentManagementBillController
import org.climatechangemakers.act.feature.cms.controller.ContentManagementIssueController
import org.climatechangemakers.act.feature.cms.controller.IssueAndBillAssociationController

fun Route.contentManagementApiRoutes(
  billController: ContentManagementBillController,
  issueController: ContentManagementIssueController,
) = route("/api") {

  route("/bills") {
    get { billController.getBills(call) }
    post { billController.postBill(call) }
    put("/{id}") { billController.updateBill(call) }
  }

  route("/issues") {
    get { issueController.getIssues(call) }
    post { TODO() }

    route("/{id}") {
      put { TODO() }
    }
  }
}