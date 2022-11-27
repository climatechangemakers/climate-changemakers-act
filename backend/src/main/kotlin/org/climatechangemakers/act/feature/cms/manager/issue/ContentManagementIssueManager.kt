package org.climatechangemakers.act.feature.cms.manager.issue

import org.climatechangemakers.act.feature.cms.model.issue.ContentManagementIssue
import org.climatechangemakers.act.feature.cms.model.issue.CreateIssue

interface ContentManagementIssueManager {

  suspend fun getIssues(): List<ContentManagementIssue>

  suspend fun updateIssue(issue: ContentManagementIssue): ContentManagementIssue

  suspend fun createIssue(issue: CreateIssue): ContentManagementIssue
}