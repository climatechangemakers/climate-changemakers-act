package org.climatechangemakers.act.feature.cms.controller

import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import org.climatechangemakers.act.feature.cms.manager.issue.ContentManagementIssueManager
import javax.inject.Inject

class ContentManagementIssueController @Inject constructor(
  private val manager: ContentManagementIssueManager,
) {

  suspend fun getIssues(call: ApplicationCall) {
    call.respond(manager.getIssues())
  }
}