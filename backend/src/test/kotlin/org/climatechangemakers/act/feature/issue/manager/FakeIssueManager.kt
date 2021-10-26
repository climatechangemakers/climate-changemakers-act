package org.climatechangemakers.act.feature.issue.manager

import kotlinx.coroutines.channels.Channel
import org.climatechangemakers.act.feature.issue.model.Issue
import org.climatechangemakers.act.feature.issue.model.PreComposedTweetResponse

class FakeIssueManager : IssueManager {
  override suspend fun getFocusIssue(): Issue {
    TODO("Not yet implemented")
  }

  override suspend fun getUnfocusedIssues(): List<Issue> {
    TODO("Not yet implemented")
  }

  val titles = Channel<String>(capacity = Channel.UNLIMITED)
  override suspend fun getIssueTitleForId(issueId: Long): String = titles.receive()

  override suspend fun getExampleStatementsForIssue(issueId: Long): List<String> {
    TODO("Not yet implemented")
  }

  override suspend fun getPreComposedTweetForIssue(
    issueId: Long,
    tweetedBioguideIds: List<String>
  ): PreComposedTweetResponse {
    TODO("Not yet implemented")
  }
}