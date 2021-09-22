package org.climatechangemakers.act.feature.issue.model

import kotlinx.serialization.Serializable

@Serializable class GetIssuesResponse(
  val focusIssue: Issue,
  val otherIssues: List<Issue>,
)