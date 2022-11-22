package org.climatechangemakers.act.feature.cms.manager.issue

import org.climatechangemakers.act.feature.cms.model.issue.ContentManagementTalkingPoint

interface ContentManagementTalkingPointsManager {

  suspend fun getTalkingPoints(issueId: Long): List<ContentManagementTalkingPoint>
}