package org.climatechangemakers.act.feature.issue.routing

import org.climatechangemakers.act.feature.issue.controller.IssueListController
import io.ktor.application.call
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.util.getOrFail

fun Route.issueRoutes(controller: IssueListController) {
  get("/issues") { controller.respondIssueList(call) }
  get("/issues/{issueId}/example-statements") {
    controller.respondExampleWhyStatements(call, call.parameters.getOrFail("issueId").toLong())
  }
}