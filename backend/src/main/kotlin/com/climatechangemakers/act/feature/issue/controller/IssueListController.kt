package com.climatechangemakers.act.feature.issue.controller

import com.climatechangemakers.act.feature.issue.manager.IssueManager
import com.climatechangemakers.act.feature.issue.model.GetIssuesResponse
import io.ktor.application.ApplicationCall
import io.ktor.response.respond
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class IssueListController @Inject constructor(
  private val manager: IssueManager,
) {

  suspend fun respondIssueList(call: ApplicationCall) = coroutineScope {
    val focusIssue = async { manager.getFocusIssue() }
    val otherIssues = async { manager.getUnfocusedIssues() }

    call.respond(
      GetIssuesResponse(focusIssue = focusIssue.await(), otherIssues = otherIssues.await())
    )
  }
}