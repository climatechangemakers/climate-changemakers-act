package com.climatechangemakers.act.feature.action.manager

interface ActionTrackerManager {

  suspend fun trackActionInitiated(email: String)

  suspend fun trackActionSendEmails(
    email: String,
    contactedBioguideIds: List<String>,
    relatedIssueId: Long,
  )
}