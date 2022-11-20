package org.climatechangemakers.act.feature.cms.manager.issue

import kotlinx.coroutines.withContext
import org.climatechangemakers.act.database.Database
import org.climatechangemakers.act.di.Io
import org.climatechangemakers.act.feature.cms.model.issue.ContentManagementIssue
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class DatabaseContentManagementIssueManager @Inject constructor(
  database: Database,
  @Io private val coroutineContext: CoroutineContext,
) : ContentManagementIssueManager {

  private val issueAndFocusQueries = database.issueAndFocusQueries

  override suspend fun getIssues(): List<ContentManagementIssue> = withContext(coroutineContext) {
    issueAndFocusQueries.selectAllActive { id, title, tweet, image, description, focused ->
      ContentManagementIssue(id, title, tweet, image, description, isFocusIssue = focused == 1L)
    }.executeAsList()
  }
}