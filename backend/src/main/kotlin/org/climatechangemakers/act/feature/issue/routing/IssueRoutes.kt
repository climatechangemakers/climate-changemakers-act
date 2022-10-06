package org.climatechangemakers.act.feature.issue.routing

import org.climatechangemakers.act.feature.issue.controller.IssueListController
import io.ktor.server.application.call
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.util.getOrFail

fun Route.issueRoutes(controller: IssueListController) = route("/issues") {
  get { controller.respondIssueList(call) }

  route("/{issueId}") {
    get("/example-statements") {
      controller.respondExampleWhyStatements(
        call,
        call.parameters.getOrFail<Long>("issueId"),
      )
    }
    get("/precomposed-tweet") {
      controller.respondPreComposedTweet(
        call,
        call.parameters.getOrFail<Long>("issueId"),
        call.request.queryParameters.getOrFail<List<String>>("bioguideIds"),
      )
    }
  }
}