package org.climatechangemakers.act.feature.action.model

import kotlinx.serialization.Serializable
import org.climatechangemakers.act.common.model.RepresentedArea
import org.climatechangemakers.act.feature.communicatewithcongress.model.Prefix
import org.climatechangemakers.act.feature.communicatewithcongress.model.Topic

@Serializable class SendEmailRequest(
  val originatingEmailAddress: String,
  val title: Prefix,
  val firstName: String,
  val lastName: String,
  val streetAddress: String,
  val city: String,
  val state: RepresentedArea,
  val postalCode: String,
  val relatedTopics: List<Topic>,
  val emailSubject: String,
  val emailBody: String,
  val relatedIssueId: Long,
  val contactedBioguideIds: List<String>,
) {
  init {
    require(contactedBioguideIds.isNotEmpty())
    require(relatedTopics.isNotEmpty())
    require(contactedBioguideIds.size <= 3)
    require(firstName.isNotBlank())
    require(lastName.isNotBlank())
  }
}