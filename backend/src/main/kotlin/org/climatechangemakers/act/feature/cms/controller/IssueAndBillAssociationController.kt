package org.climatechangemakers.act.feature.cms.controller

import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import org.climatechangemakers.act.feature.cms.manager.issue.IssueAndBillAssociationManager
import javax.inject.Inject

class IssueAndBillAssociationController @Inject constructor(
  private val manager: IssueAndBillAssociationManager,
) {

  suspend fun getBillsForIssue(call: ApplicationCall) {
    val issueId = checkNotNull(call.parameters["id"]?.toLong())
    call.respond(manager.getBillsForIssueId(issueId))
  }
}