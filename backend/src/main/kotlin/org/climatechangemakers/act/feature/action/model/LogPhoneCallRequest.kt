package org.climatechangemakers.act.feature.action.model

import kotlinx.serialization.Serializable

@Serializable class LogPhoneCallRequest(
  val originatingEmailAddress: String,
  val relatedIssueId: Long,
  val contactedPhoneNumber: String,
  val contactedBioguideId: String,
)