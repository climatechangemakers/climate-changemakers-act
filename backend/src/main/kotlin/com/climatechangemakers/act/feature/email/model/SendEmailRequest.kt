package com.climatechangemakers.act.feature.email.model

import kotlinx.serialization.Serializable

@Serializable class SendEmailRequest(
  val relatedIssueId: Int,
  val emailBody: String,
  val contactedBioguideIds: List<String>,
)