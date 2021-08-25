package com.climatechangemakers.act.feature.findlegislator.manager

import com.climatechangemakers.act.feature.findlegislator.model.GeocodioApiResult
import com.climatechangemakers.act.feature.findlegislator.model.GeocodioLegislator
import com.climatechangemakers.act.feature.findlegislator.model.GetLegislatorsByAddressRequest
import com.climatechangemakers.act.feature.findlegislator.model.Legislator
import com.climatechangemakers.act.feature.findlegislator.model.LegislatorArea
import com.climatechangemakers.act.feature.findlegislator.model.LegislatorRole
import com.climatechangemakers.act.feature.findlegislator.service.GeocodioService
import com.climatechangemakers.act.feature.lcvscore.manager.LcvScoreManager
import com.climatechangemakers.act.feature.lcvscore.model.LcvScore
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class LegislatorsManager @Inject constructor(
  private val geocodioService: GeocodioService,
  private val lcvScoreManager: LcvScoreManager,
) {

  @OptIn(ExperimentalStdlibApi::class)
  suspend fun getLegislators(request: GetLegislatorsByAddressRequest): List<Legislator> = coroutineScope {
    val geoCodioResponse: GeocodioApiResult = geocodioService.geocode(query = request.queryString)

    val congressionalDistrict = geoCodioResponse.results
      .flatMap { it.fields.congressionalDistricts }
      .first()

    congressionalDistrict.currentLegislators.map { geocodioLegislator ->
      async {
        val lcvScores = coroutineScope {
          val lifetimeScore = async { lcvScoreManager.getLifetimeScore(geocodioLegislator.references.bioguide) }
          val yearlyScores = async { lcvScoreManager.getYearlyScores(geocodioLegislator.references.bioguide) }

          buildList {
            lifetimeScore.await()?.let(::add)
            addAll(yearlyScores.await())
          }
        }

        geocodioLegislator.toDomainLegislator(
          state = request.state,
          districtNumber = congressionalDistrict.districtNumber,
          lcvScores = lcvScores.also { check(it.isNotEmpty()) }
        )
      }
    }.awaitAll()
  }
}

private val GetLegislatorsByAddressRequest.queryString: String get() = "$streetAddress, $city $state $postalCode"

private val GeocodioLegislator.fullName get() = "${bio.firstName} ${bio.lastName}"

private fun GeocodioLegislator.toDomainLegislator(
  state: String,
  districtNumber: Int,
  lcvScores: List<LcvScore>,
) = Legislator(
  name = fullName,
  role = type,
  siteUrl = contactInfo.siteUrl,
  phone = contactInfo.phone,
  imageUrl = imageUrl,
  area = LegislatorArea(state = state, if (type == LegislatorRole.Senator) null else districtNumber),
  lcvScores = lcvScores,
)

private val GeocodioLegislator.imageUrl: String get() {
  val bioguide = references.bioguide
  return "https://bioguide.congress.gov/bioguide/photo/${bioguide[0]}/$bioguide.jpg"
}