package org.climatechangemakers.act.feature.cms.manager.issue

import kotlinx.coroutines.withContext
import org.climatechangemakers.act.common.extension.executeAsOneOrNotFound
import org.climatechangemakers.act.database.Database
import org.climatechangemakers.act.di.Io
import org.climatechangemakers.act.feature.cms.manager.bill.ContentManagementBillManager
import org.climatechangemakers.act.feature.cms.model.issue.ContentManagementIssue
import org.climatechangemakers.act.feature.cms.model.issue.ContentManagementTalkingPoint
import org.climatechangemakers.act.feature.cms.model.issue.CreateIssue
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

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
    TODO()
//    val currentIssue = issueAndFocusQueries
//      .selectForId(issue.id, ::toCmsIssue)
//      .executeAsOneOrNotFound()
//
//    if (issue.isFocusIssue && !currentIssue.isFocusIssue) {
//      // Issue focus state has changed. Update it.
//      focusIssueQueries.insert(issue.id)
//    }
//
//    issueQueries.updateIssue(
//      id = issue.id,
//      title = issue.title,
//      tweet = issue.precomposedTweetTemplate,
//      imageUrl = issue.imageUrl,
//      description = issue.description,
//    )
//    issueAndFocusQueries.selectForId(
//      id = issue.id,
//      mapper = ::toCmsIssue
//    ).executeAsOneOrNotFound()
  }

  override suspend fun createIssue(
    issue: CreateIssue
  ): ContentManagementIssue = withContext(coroutineContext) {
//    val issueId = issueQueries.insertIssue(
//      title = issue.title,
//      precomposedTweet = issue.precomposedTweetTemplate,
//      imageUrl = issue.imageUrl,
//      description = issue.description,
//    ).executeAsOne()
//
//    if (issue.isFocusIssue) {
//      focusIssueQueries.insert(issueId)
//    }
//
//    issueAndFocusQueries.selectForId(issueId, ::toCmsIssue).executeAsOneOrNotFound()

    TODO()
  }

//  private fun toCmsIssue(
//    id: Long,
//    title: String,
//    tweet: String,
//    image: String,
//    description: String,
//    focused: Long,
//  ) = ContentManagementIssue(id, title, tweet, image, description, isFocusIssue = focused == 1L)
}