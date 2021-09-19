package com.climatechangemakers.act.feature.findlegislator.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable class GeocodioApiResult(
  val results: List<GeocodeResult> = emptyList()
)

@Serializable class GeocodeResult(
  val location: Location,
  val fields: Fields,
)

@Serializable class Fields(
  @SerialName("congressional_districts") val congressionalDistricts: List<CongressionalDistrict> = emptyList()
)

@Serializable class CongressionalDistrict(
  val name: String,
  @SerialName("district_number") val districtNumber: Int,
  @SerialName("current_legislators") val currentLegislators: List<GeocodioLegislator> = emptyList(),
)

@Serializable class GeocodioLegislator(
  val type: LegislatorRole,
  val bio: LegislatorBio,
  @SerialName("contact") val contactInfo: LegislatorContactInformation,
  val references: LegislatorReferences,
)

@Serializable class LegislatorBio(
  @SerialName("last_name") val lastName: String,
  @SerialName("first_name") val firstName: String,
  val party: GeocodioPoliticalParty,
)

@Serializable enum class GeocodioPoliticalParty {
  @SerialName("Republican") Republican,
  @SerialName("Democrat") Democrat,
  @SerialName("Independent") Independent,
  @SerialName("Libertarian") Libertarian,
}

val GeocodioPoliticalParty.domainPoliticalParty: LegislatorPoliticalParty get() = when (this) {
  GeocodioPoliticalParty.Republican -> LegislatorPoliticalParty.Republican
  GeocodioPoliticalParty.Democrat -> LegislatorPoliticalParty.Democrat
  GeocodioPoliticalParty.Independent -> LegislatorPoliticalParty.Independent
  GeocodioPoliticalParty.Libertarian -> LegislatorPoliticalParty.Libertarian
}

@Serializable class LegislatorContactInformation(
  @SerialName("url") val siteUrl: String,
  @SerialName("address") val formattedAddress: String,
  val phone: String,
)

@Serializable class LegislatorReferences(
  @SerialName("bioguide_id") val bioguide: String,
)