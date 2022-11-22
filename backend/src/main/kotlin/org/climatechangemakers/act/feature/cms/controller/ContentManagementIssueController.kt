package org.climatechangemakers.act.feature.cms.controller

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import org.climatechangemakers.act.feature.cms.manager.issue.ContentManagementIssueManager
import org.climatechangemakers.act.feature.cms.model.issue.ContentManagementIssue
import org.climatechangemakers.act.feature.cms.model.issue.CreateIssue
import javax.inject.Inject

class ContentManagementIssueController @Inject constructor(
  private val manager: ContentManagementIssueManager,
) {

  suspend fun getIssues(call: ApplicationCall) {
    call.respond(manager.getIssues())
  }

  suspend fun updateIssue(call: ApplicationCall) {
    val issue = call.receive<ContentManagementIssue>()
    val issueId = checkNotNull(call.parameters["id"]?.toLong())

    if (issueId == issue.id) {
      call.respond(manager.updateIssue(issue))
    } else {
      call.respond(
        status = HttpStatusCode.BadRequest,
        message = "Attempting to update issue ${issue.id} at path $issueId.",
      )
    }
  }

  suspend fun createIssue(call: ApplicationCall) {
    val issue = call.receive<CreateIssue>()
    call.respond(manager.createIssue(issue))
  }
}