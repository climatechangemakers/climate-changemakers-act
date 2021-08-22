package com.climatechangemakers.act.feature.issue.routing

import com.climatechangemakers.act.feature.issue.controller.IssueListController
import io.ktor.application.call
import io.ktor.routing.Route
import io.ktor.routing.get

fun Route.issueRoutes(controller: IssueListController) {
  get("/issues") { controller.respondIssueList(call) }
}