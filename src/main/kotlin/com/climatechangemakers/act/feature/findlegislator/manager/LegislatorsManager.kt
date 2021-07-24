package com.climatechangemakers.act.feature.findlegislator.manager

import com.climatechangemakers.act.feature.congressgov.manager.SearchCongressManager
import com.climatechangemakers.act.feature.findlegislator.model.GeocodioApiResult
import com.climatechangemakers.act.feature.findlegislator.model.GetLegislatorsRequest
import com.climatechangemakers.act.feature.findlegislator.model.GeocodioLegislator
import com.climatechangemakers.act.feature.findlegislator.model.Legislator
import com.climatechangemakers.act.feature.findlegislator.service.GeocodioService
import com.climatechangemakers.act.feature.lcvscore.manager.LcvScoreManager
import com.climatechangemakers.act.feature.lcvscore.model.LcvScore
import com.climatechangemakers.act.feature.lcvscore.model.LcvScoreType
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class LegislatorsManager @Inject constructor(
  private val geocodioService: GeocodioService,
  private val lcvScoreManager: LcvScoreManager,
  private val searchCongressManager: SearchCongressManager,
) {

  suspend fun getLegislators(request: GetLegislatorsRequest): List<Legislator> = coroutineScope {
    val geoCodioResponse: GeocodioApiResult = geocodioService.getLegislators(query = request.queryString)

    val geocodioLegislators = geoCodioResponse.results
      .flatMap { it.fields.congressionalDistricts }
      .first()
      .currentLegislators

    geocodioLegislators.map { geocodioLegislator ->
      async {
        val legislatorImageUrl = async { searchCongressManager.getLegislatorImage() }
        val lcvScores = async {
          lcvScoreManager.getScores(geocodioLegislator.fullName).also { scores ->
            // ensure we at least have a lifetime score for this individual
            checkNotNull(scores.firstOrNull { it.scoreType == LcvScoreType.LifetimeScore })
          }
        }

        geocodioLegislator.toDomainLegislator(lcvScores.await(), legislatorImageUrl.await())
      }
    }.awaitAll()
  }
}

private val GetLegislatorsRequest.queryString: String get() = "$streetAddress, $city $state $postalCode"

private val GeocodioLegislator.fullName get() = "${bio.firstName} ${bio.lastName}"

private fun GeocodioLegislator.toDomainLegislator(lcvScores: List<LcvScore>, imageUrl: String) = Legislator(
  name = fullName,
  type = type,
  siteUrl = contactInfo.siteUrl,
  phone = contactInfo.phone,
  imageUrl = imageUrl,
  lcvScores = lcvScores,
)