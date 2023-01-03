package org.climatechangemakers.act.feature.cms.routing

import io.ktor.server.http.content.defaultResource
import io.ktor.server.http.content.resource
import io.ktor.server.http.content.resources
import io.ktor.server.http.content.static
import io.ktor.server.http.content.staticBasePackage
import io.ktor.server.http.content.staticRootFolder
import io.ktor.server.routing.Route
import io.ktor.server.routing.route
import org.climatechangemakers.act.feature.cms.controller.ContentManagementBillController
import org.climatechangemakers.act.feature.cms.controller.ContentManagementIssueController

fun Route.contentManagementRoutes(
  billController: ContentManagementBillController,
  issueController: ContentManagementIssueController,
) = cmsAuthenticated {
  route("/cms") {
    contentManagementApiRoutes(
      billController,
      issueController,
    )
    static {
      resources("cms")
      defaultResource("cms/index.html")
    }
  }
}