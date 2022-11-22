package org.climatechangemakers.act.feature.cms.routing

import io.ktor.server.routing.Route
import io.ktor.server.routing.route
import org.climatechangemakers.act.feature.cms.controller.ContentManagementBillController
import org.climatechangemakers.act.feature.cms.controller.ContentManagementIssueController
import org.climatechangemakers.act.feature.cms.controller.ContentManagementTalkingPointsController

fun Route.contentManagementRoutes(
  billController: ContentManagementBillController,
  issueController: ContentManagementIssueController,
  talkingPointsController: ContentManagementTalkingPointsController,
) = cmsAuthenticated {
  route("/cms") {
    contentManagementApiRoutes(
      billController,
      issueController,
      talkingPointsController
    )
  }
}