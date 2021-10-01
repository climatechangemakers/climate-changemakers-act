package org.climatechangemakers.act.feature.action.model

import kotlinx.serialization.Serializable

@Serializable class LogTweetRequest(
  val originatingEmailAddress: String,
  val relatedIssueId: Long,
  val contactedBioguideIds: List<String>,
)