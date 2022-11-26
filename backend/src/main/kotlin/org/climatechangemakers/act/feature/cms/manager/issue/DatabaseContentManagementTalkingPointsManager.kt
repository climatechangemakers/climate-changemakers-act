package org.climatechangemakers.act.feature.cms.manager.issue

import kotlinx.coroutines.withContext
import org.climatechangemakers.act.common.util.exists
import org.climatechangemakers.act.database.Database
import org.climatechangemakers.act.di.Io
import org.climatechangemakers.act.feature.cms.model.issue.ContentManagementTalkingPoint
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class DatabaseContentManagementTalkingPointsManager @Inject constructor(
  database: Database,
  @Io private val coroutineContext: CoroutineContext,
) : ContentManagementTalkingPointsManager {

  private val talkingPointQueries = database.talkingPointQueries
  private val issueAndFocusQueries = database.issueAndFocusQueries

  override suspend fun getTalkingPoints(
    issueId: Long
  ): List<ContentManagementTalkingPoint> = withContext(coroutineContext) {
    exists(issueAndFocusQueries.rowCount(issueId).executeAsOne() == 1L) {
      "Issue with id $issueId does not exist."
    }

    talkingPointQueries.selectForIssueId(
      issueId = issueId,
      mapper = ::ContentManagementTalkingPoint,
    ).executeAsList()
  }

  override suspend fun updateTalkingPoints(
    issueId: Long,
    talkingPoints: List<ContentManagementTalkingPoint>
  ): List<ContentManagementTalkingPoint> = withContext(coroutineContext) {
    talkingPointQueries.deleteForIssue(issueId)
    talkingPoints.forEach { tp ->
      talkingPointQueries.insert(
        title = tp.title,
        issueId = issueId,
        content = tp.content,
        relativeOrderPosition = tp.relativeOrderPosition,
      )
    }
    talkingPointQueries.selectForIssueId(
      issueId = issueId,
      mapper = ::ContentManagementTalkingPoint,
    ).executeAsList()
  }
}