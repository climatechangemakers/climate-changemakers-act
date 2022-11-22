package org.climatechangemakers.act.feature.cms.controller

import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import org.climatechangemakers.act.feature.cms.manager.issue.ContentManagementTalkingPointsManager
import javax.inject.Inject

class ContentManagementTalkingPointsController @Inject constructor(
  private val manager: ContentManagementTalkingPointsManager,
) {

  suspend fun getTalkingPointsForIssue(call: ApplicationCall) {
    val issueId = checkNotNull(call.parameters["id"]?.toLong())
    call.respond(manager.getTalkingPoints(issueId))
  }
}