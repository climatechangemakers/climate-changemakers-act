package org.climatechangemakers.act.feature.cms.manager.issue

import org.climatechangemakers.act.feature.cms.model.issue.ContentManagementIssue

interface ContentManagementIssueManager {

  suspend fun getIssues(): List<ContentManagementIssue.Persisted>

  suspend fun updateIssue(
    issue: ContentManagementIssue.Persisted
  ): ContentManagementIssue.Persisted

  suspend fun createIssue(
    issue: ContentManagementIssue.New
  ): ContentManagementIssue.Persisted
}