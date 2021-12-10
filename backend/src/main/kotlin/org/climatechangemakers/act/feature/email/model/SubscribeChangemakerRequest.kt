package org.climatechangemakers.act.feature.email.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.climatechangemakers.act.common.model.RepresentedArea

@Serializable data class SubscribeChangemakerRequest private constructor(
  @SerialName("email_address") val email: String,
  @SerialName("merge_fields") val mergeFields: MailchimpMergeFields,
) {
  val tags = listOf("U.S.")
  val status = "subscribed"
  @SerialName("email_type") val emailType = "html"

  constructor(email: String, firstName: String, lastName: String, state: RepresentedArea) : this(
    email = email,
    mergeFields = MailchimpMergeFields(firstName = firstName, lastName = lastName, state = state)
  )
}

@Serializable data class MailchimpMergeFields(
  @SerialName("FNAME") val firstName: String,
  @SerialName("LNAME") val lastName: String,

  // TODO(kcianfarini) this is garbage and mailchimp needs to be cleaned up by the product people.
  @SerialName("STATE") val state: RepresentedArea,
  @SerialName("SELECT1FD") val state2: RepresentedArea = state,
  @SerialName("SELECTYUI") val state3: RepresentedArea = state,
)