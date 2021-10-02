package org.climatechangemakers.act.feature.issue.manager

import org.climatechangemakers.act.feature.issue.model.Issue

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
   * Get a list of example "Why" statements that explain why this issue is important
   * to a constituent.
   */
  suspend fun getExampleStatementsForIssue(issueId: Long): List<String>
}