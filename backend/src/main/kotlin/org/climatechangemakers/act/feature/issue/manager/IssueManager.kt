package org.climatechangemakers.act.feature.issue.manager

import org.climatechangemakers.act.feature.issue.model.Issue
import org.climatechangemakers.act.feature.issue.model.PreComposedTweetResponse

interface IssueManager {

  /**
   * Get the current focus [Issue] at the time of the request.
   */
  suspend fun getFocusIssue(): Issue

  /**
   * Get all [Issue] instances that are not focused at the time of the request.
   */
  suspend fun getUnfocusedIssues(): List<Issue>

  /**
   * Get the [Issue.title] corresponding to [issueId].
   */
  suspend fun getIssueTitleForId(issueId: Long): String

  /**
   * Get a list of example "Why" statements that explain why this issue is important
   * to a constituent.
   */
  suspend fun getExampleStatementsForIssue(issueId: Long): List<String>

  /**
   * Get the designated pre-composed tweet for the given [issueId].
   */
  suspend fun getPreComposedTweetForIssue(
    issueId: Long,
    tweetedBioguideIds: List<String>,
  ): PreComposedTweetResponse
}