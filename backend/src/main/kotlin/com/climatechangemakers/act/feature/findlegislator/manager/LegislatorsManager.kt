package com.climatechangemakers.act.feature.findlegislator.manager

import com.climatechangemakers.act.feature.findlegislator.model.GeocodioApiResult
import com.climatechangemakers.act.feature.findlegislator.model.GeocodioLegislator
import com.climatechangemakers.act.feature.findlegislator.model.GetLegislatorsByAddressRequest
import com.climatechangemakers.act.feature.findlegislator.model.Legislator
import com.climatechangemakers.act.feature.findlegislator.service.GeocodioService
import com.climatechangemakers.act.feature.lcvscore.manager.LcvScoreManager
import com.climatechangemakers.act.feature.lcvscore.model.LcvScore
import com.climatechangemakers.act.feature.lcvscore.model.LcvScoreType
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class LegislatorsManager @Inject constructor(
  private val geocodioService: GeocodioService,
  private val lcvScoreManager: LcvScoreManager,
) {

  suspend fun getLegislators(request: GetLegislatorsByAddressRequest): List<Legislator> = coroutineScope {
    val geoCodioResponse: GeocodioApiResult = geocodioService.geocode(query = request.queryString)

    val geocodioLegislators = geoCodioResponse.results
      .flatMap { it.fields.congressionalDistricts }
      .first()
      .currentLegislators

    geocodioLegislators.map { geocodioLegislator ->
      async {
        val lcvScores = lcvScoreManager.getScores(geocodioLegislator.references.bioguide).also { scores ->
          check(scores.isNotEmpty())
        }

        geocodioLegislator.toDomainLegislator(lcvScores)
      }
    }.awaitAll()
  }
}

private val GetLegislatorsByAddressRequest.queryString: String get() = "$streetAddress, $city $state $postalCode"

private val GeocodioLegislator.fullName get() = "${bio.firstName} ${bio.lastName}"

private fun GeocodioLegislator.toDomainLegislator(lcvScores: List<LcvScore>) = Legislator(
  name = fullName,
  role = type,
  siteUrl = contactInfo.siteUrl,
  phone = contactInfo.phone,
  imageUrl = imageUrl,
  lcvScores = lcvScores,
)

private val GeocodioLegislator.imageUrl: String get() {
  val bioguide = references.bioguide
  return "https://bioguide.congress.gov/bioguide/photo/${bioguide[0]}/$bioguide.jpg"
}