package org.climatechangemakers.act.feature.cms.manager.issue

import org.climatechangemakers.act.feature.cms.model.issue.ContentManagementIssue

interface ContentManagementIssueManager {

  suspend fun getIssues(): List<ContentManagementIssue>
}