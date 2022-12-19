package org.climatechangemakers.act.feature.cms.manager.issue

import kotlinx.coroutines.withContext
import org.climatechangemakers.act.common.extension.executeAsOneOrNotFound
import org.climatechangemakers.act.database.Database
import org.climatechangemakers.act.di.Io
import org.climatechangemakers.act.feature.cms.model.issue.ContentManagementIssue
import org.climatechangemakers.act.feature.cms.model.issue.ContentManagementTalkingPoint
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

// TODO(kcianfarini) Break this up into multiple managers that can share a suspending transaction
//                   once we migrate to a non-blocking pgsql driver.
class DatabaseContentManagementIssueManager @Inject constructor(
  database: Database,
  @Io private val coroutineContext: CoroutineContext,
) : ContentManagementIssueManager {

  private val issueAndFocusQueries = database.issueAndFocusQueries
  private val issueQueries = database.issueQueries
  private val focusIssueQueries = database.focusIssueQueries
  private val billQueries = database.congressBillAndIssueQueries
  private val talkingPointQueries = database.talkingPointQueries

  override suspend fun getIssues(): List<ContentManagementIssue> = withContext(coroutineContext) {
    issueAndFocusQueries.selectAllActive().executeAsList().map { issue ->
      val relatedBillIds = billQueries.selectBillIdsForIssueId(issue.id).executeAsList()
      val talkingPoints = talkingPointQueries.selectForIssueId(issue.id, ::ContentManagementTalkingPoint).executeAsList()
      ContentManagementIssue(
        id = issue.id,
        title = issue.title,
        precomposedTweetTemplate = issue.precomposed_tweet_template,
        imageUrl = issue.image_url,
        description = issue.description,
        isFocusIssue = issue.is_focused_int == 1L,
        relatedBillIds = relatedBillIds,
        talkingPoints = talkingPoints,
      )
    }
  }

  override suspend fun updateIssue(
    issue: ContentManagementIssue
  ): ContentManagementIssue = withContext(coroutineContext) {
    issueQueries.transactionWithResult {
      issueQueries.updateIssue(
        title = issue.title,
        tweet = issue.precomposedTweetTemplate,
        imageUrl = issue.imageUrl,
        description = issue.description,
        id = requireNotNull(issue.id),
      )

      val currentIssue = issueAndFocusQueries
        .selectForId(issue.id)
        .executeAsOneOrNotFound()

      if (issue.isFocusIssue && currentIssue.is_focused_int != 1L) {
        // Issue focus state has changed. Update it.
        focusIssueQueries.insert(issue.id)
      }

      billQueries.deleteForIssueId(issue.id)
      issue.relatedBillIds.forEach { billId ->
        billQueries.insert(issueId = issue.id, billId = billId)
      }

      talkingPointQueries.deleteForIssue(issue.id)
      issue.talkingPoints.forEach { tp ->
        talkingPointQueries.insert(
          title = tp.title,
          issueId = issue.id,
          content = tp.content,
          relativeOrderPosition = tp.relativeOrderPosition
        )
      }

      issue
    }
  }

  override suspend fun createIssue(
    issue: ContentManagementIssue
  ): ContentManagementIssue = withContext(coroutineContext) {
    require(issue.id == null)
    issueQueries.transactionWithResult {
      val issueId = issueQueries.insertIssue(
        title = issue.title,
        precomposedTweet = issue.precomposedTweetTemplate,
        imageUrl = issue.imageUrl,
        description = issue.description,
      ).executeAsOne()

      if (issue.isFocusIssue) {
        focusIssueQueries.insert(issueId)
      }

      issue.relatedBillIds.forEach { billId ->
        billQueries.insert(issueId = issueId, billId = billId)
      }

      issue.talkingPoints.forEach { talkingPoint ->
        talkingPointQueries.insert(
          title = talkingPoint.title,
          issueId = issueId,
          content = talkingPoint.content,
          relativeOrderPosition = talkingPoint.relativeOrderPosition
        )
      }

      issue.copy(id = issueId)
    }
  }
}