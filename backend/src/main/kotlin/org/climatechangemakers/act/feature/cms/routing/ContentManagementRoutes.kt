package org.climatechangemakers.act.feature.cms.routing

import io.ktor.server.routing.Route
import io.ktor.server.routing.route
import org.climatechangemakers.act.feature.cms.controller.ContentManagementBillController
import org.climatechangemakers.act.feature.cms.controller.ContentManagementIssueController
import org.climatechangemakers.act.feature.cms.controller.IssueAndBillAssociationController

fun Route.contentManagementRoutes(
  billController: ContentManagementBillController,
  issueController: ContentManagementIssueController,
) = cmsAuthenticated {
  route("/cms") {
    contentManagementApiRoutes(
      billController,
      issueController,
    )
  }
}