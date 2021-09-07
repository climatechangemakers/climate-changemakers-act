package com.climatechangemakers.act.feature.issue.manager

import com.climatechangemakers.act.database.Database
import com.climatechangemakers.act.di.Io
import com.climatechangemakers.act.feature.issue.model.Issue
import com.climatechangemakers.act.feature.issue.model.TalkingPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class DatabaseIssueManager @Inject constructor(
  database: Database,
  @Io private val ioDispatcher: CoroutineContext,
) : IssueManager {

  private val issueQueries = database.issueAndFocusQueries
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

  private suspend fun getIssueTalkingPoints(issueId: Long): List<TalkingPoint> = withContext(ioDispatcher) {
    talkingPointQueries.selectForIssueId(issueId, ::TalkingPoint).executeAsList()
  }
}