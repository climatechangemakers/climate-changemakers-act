package org.climatechangemakers.act.feature.issue.controller

import org.climatechangemakers.act.feature.issue.manager.IssueManager
import org.climatechangemakers.act.feature.issue.model.GetIssuesResponse
import io.ktor.application.ApplicationCall
import io.ktor.response.respond
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.climatechangemakers.act.feature.issue.model.Issue
import org.slf4j.Logger
import javax.inject.Inject
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

class IssueListController @Inject constructor(
  private val manager: IssueManager,
  private val logger: Logger,
) {

  @OptIn(ExperimentalTime::class)
  suspend fun respondIssueList(call: ApplicationCall) {
    val (pair, timedValue) = measureTimedValue { getIssues() }
    logger.debug("Loading issues took ${timedValue.inWholeMilliseconds}")

    call.respond(GetIssuesResponse(focusIssue = pair.first, otherIssues = pair.second))
  }

  suspend fun respondExampleWhyStatements(call: ApplicationCall, issueId: Long) = call.respond(
    manager.getExampleStatementsForIssue(issueId)
  )

  suspend fun respondPreComposedTweet(call: ApplicationCall, issueId: Long, bioguideIds: List<String>) {
    call.respond(
      manager.getPreComposedTweetForIssue(issueId, bioguideIds)
    )
  }

  private suspend fun getIssues(): Pair<Issue, List<Issue>> = coroutineScope {
    val focusIssue = async { manager.getFocusIssue() }
    val otherIssues = async { manager.getUnfocusedIssues() }

    Pair(focusIssue.await(), otherIssues.await())
  }
}