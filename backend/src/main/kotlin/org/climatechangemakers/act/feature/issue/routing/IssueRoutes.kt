package org.climatechangemakers.act.feature.issue.routing

import org.climatechangemakers.act.feature.issue.controller.IssueListController
import io.ktor.application.call
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.util.getOrFail

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