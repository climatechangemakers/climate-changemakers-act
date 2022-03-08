package org.climatechangemakers.act.feature.action.manager

interface ActionTrackerManager {

  suspend fun trackActionInitiated(
    email: String,
    optedIntoNewsletter: Boolean,
  )

  suspend fun trackActionSendEmail(
    email: String,
    contactedBioguideId: String,
    relatedIssueId: Long,
    emailDeliveryId: String,
  )

  suspend fun trackActionPhoneCall(
    email: String,
    contactedBioguideId: String,
    relatedIssueId: Long,
  )

  suspend fun trackTweet(
    email: String,
    contactedBioguideIds: List<String>,
    relatedIssueId: Long,
  )

  suspend fun trackActionSignUp(
    email: String,
  )
}