package com.climatechangemakers.act.feature.representativefinder.manager

import com.climatechangemakers.act.feature.representativefinder.model.GeocodioApiResult
import com.climatechangemakers.act.feature.representativefinder.model.GetRepresentativeRequest
import com.climatechangemakers.act.feature.representativefinder.model.GeocodioLegislator
import com.climatechangemakers.act.feature.representativefinder.model.Legislator
import com.climatechangemakers.act.feature.representativefinder.service.GeocodioService
import javax.inject.Inject

class LegislatorsManager @Inject constructor(
  private val client: GeocodioService,
) {

  suspend fun getLegislators(request: GetRepresentativeRequest): List<Legislator> {
    val geoCodioResponse: GeocodioApiResult = client.getLegislators(query = request.queryString)

    return geoCodioResponse.results
      .flatMap { it.fields.congressionalDistricts }
      .first()
      .currentLegislators
      .map(GeocodioLegislator::toDomainLegislator)
  }
}

private val GetRepresentativeRequest.queryString: String get() = "$streetAddress, $city $state $postalCode"

private fun GeocodioLegislator.toDomainLegislator() = Legislator(
  name = "${bio.firstName} ${bio.lastName}",
  type = type,
  siteUrl = contactInfo.siteUrl,
  phone = contactInfo.phone,
)