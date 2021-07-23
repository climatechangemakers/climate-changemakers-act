package com.climatechangemakers.act.feature.findlegislator.manager

import com.climatechangemakers.act.feature.findlegislator.model.GeocodioApiResult
import com.climatechangemakers.act.feature.findlegislator.model.GetLegislatorsRequest
import com.climatechangemakers.act.feature.findlegislator.model.GeocodioLegislator
import com.climatechangemakers.act.feature.findlegislator.model.Legislator
import com.climatechangemakers.act.feature.findlegislator.service.GeocodioService
import javax.inject.Inject

class LegislatorsManager @Inject constructor(
  private val client: GeocodioService,
) {

  suspend fun getLegislators(request: GetLegislatorsRequest): List<Legislator> {
    val geoCodioResponse: GeocodioApiResult = client.getLegislators(query = request.queryString)

    return geoCodioResponse.results
      .flatMap { it.fields.congressionalDistricts }
      .first()
      .currentLegislators
      .map(GeocodioLegislator::toDomainLegislator)
  }
}

private val GetLegislatorsRequest.queryString: String get() = "$streetAddress, $city $state $postalCode"

private fun GeocodioLegislator.toDomainLegislator() = Legislator(
  name = "${bio.firstName} ${bio.lastName}",
  type = type,
  siteUrl = contactInfo.siteUrl,
  phone = contactInfo.phone,
)