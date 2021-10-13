package org.climatechangemakers.act.feature.issue.manager

import org.climatechangemakers.act.database.Database
import org.climatechangemakers.act.di.Io
import org.climatechangemakers.act.feature.issue.model.Issue
import org.climatechangemakers.act.feature.issue.model.TalkingPoint
import org.climatechangemakers.act.feature.issue.model.PreComposedTweetResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.climatechangemakers.act.common.util.exists
import org.climatechangemakers.act.common.util.joinToPhrase
import org.climatechangemakers.act.feature.findlegislator.manager.MemberOfCongressManager
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class DatabaseIssueManager @Inject constructor(
  private val memberOfCongressManager: MemberOfCongressManager,
  database: Database,
  @Io private val ioDispatcher: CoroutineContext,
) : IssueManager {

  private val issueQueries = database.issueAndFocusQueries
  private val exampleIssueWhyStatementQueries = database.exampleIssueWhyStatementQueries
  private val talkingPointQueries = database.talkingPointQueries

  override suspend fun getFocusIssue(): Issue = withContext(ioDispatcher) {
    val issue = issueQueries.selectFocused().executeAsOne()
    Issue(id = issue.id, title = issue.title, talkingPoints = getIssueTalkingPoints(issue.id))
  }

  override suspend fun getUnfocusedIssues(): List<Issue> = withContext(ioDispatcher) {
    issueQueries.selectUnfocused().executeAsList().map { issue ->
      async { Issue(issue.id, issue.title, getIssueTalkingPoints(issue.id)) }
    }.awaitAll()
  }

  override suspend fun getExampleStatementsForIssue(issueId: Long): List<String> = withContext(ioDispatcher) {
    ensureIssueExists(issueId)
    exampleIssueWhyStatementQueries.selectForIssueId(issueId).executeAsList()
  }

  override suspend fun getPreComposedTweetForIssue(
    issueId: Long,
    tweetedBioguideIds: List<String>,
  ): PreComposedTweetResponse {
    val memberHandles = coroutineScope {
      tweetedBioguideIds.map { id ->
        async { "@${memberOfCongressManager.getMemberOfCongressForBioguide(id).twitterHandle}" }
      }.awaitAll().joinToPhrase()
    }
    return withContext(ioDispatcher) {
      PreComposedTweetResponse(issueQueries.selectTweetTemplate(issueId).executeAsOne().format(memberHandles))
    }
  }

  private suspend fun getIssueTalkingPoints(issueId: Long): List<TalkingPoint> = withContext(ioDispatcher) {
    ensureIssueExists(issueId)
    talkingPointQueries.selectForIssueId(issueId, ::TalkingPoint).executeAsList()
  }

  private suspend fun ensureIssueExists(issueId: Long) = withContext(ioDispatcher) {
    exists(issueQueries.rowCount(issueId).executeAsOne() == 1L) { "No issue with id $issueId was found" }
  }
}
