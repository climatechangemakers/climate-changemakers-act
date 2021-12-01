package org.climatechangemakers.act.feature.membership.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.climatechangemakers.act.common.model.RepresentedArea
import org.climatechangemakers.act.common.serializers.YesNoBooleanSerializer

@Serializable class AirtableCreateRecordRequest private constructor(
  val records: List<AirtableCreateRecord>,
) {
  constructor(
    email: String,
    firstName: String,
    lastName: String,
    city: String,
    state: RepresentedArea,
    experience: Boolean,
    referral: String,
  ) : this(
    listOf(
      AirtableCreateRecord(
        AirtableCreateRecordFields(
          email,
          firstName,
          lastName,
          city,
          state,
          referral,
          experience,
        )
      )
    )
  )
}

@Serializable class AirtableCreateRecord(
  val fields: AirtableCreateRecordFields,
)

@Serializable class AirtableCreateRecordFields(
  @SerialName("Email") val email: String,
  @SerialName("First Name") val firstName: String,
  @SerialName("Last Name") val lastName: String,
  @SerialName("City") val city: String,
  @SerialName("State") val state: RepresentedArea,
  @SerialName("Referral?") val referral: String,

  @SerialName("Experience?")
  @Serializable(with = YesNoBooleanSerializer::class)
  val experience: Boolean,
) {
  @SerialName("Country") val country = "USA"
}