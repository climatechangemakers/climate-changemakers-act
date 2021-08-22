package com.climatechangemakers.act.feature.issue.manager

import com.climatechangemakers.act.feature.issue.model.Issue

interface IssueManager {

  /**
   * Get the current focus [Issue] at the time of the request.
   */
  suspend fun getFocusIssue(): Issue

  /**
   * Get all [Issue] instances that are not focused at the time of the request.
   */
  suspend fun getUnfocusedIssues(): List<Issue>
}