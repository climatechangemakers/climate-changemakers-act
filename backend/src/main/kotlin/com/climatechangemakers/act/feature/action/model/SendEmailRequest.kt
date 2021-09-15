package com.climatechangemakers.act.feature.action.model

import kotlinx.serialization.Serializable

@Serializable class SendEmailRequest(
  val originatingEmailAddress: String,
  val relatedIssueId: Long,
  val emailBody: String,
  val contactedBioguideIds: List<String>,
)