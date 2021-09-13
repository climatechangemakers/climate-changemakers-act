package com.climatechangemakers.act.feature.email.model

import kotlinx.serialization.Serializable

@Serializable class SendEmailRequest(
  val originatingEmailAddress: String,
  val relatedIssueId: Long,
  val emailBody: String,
  val contactedBioguideIds: List<String>,
)