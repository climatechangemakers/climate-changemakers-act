package com.climatechangemakers.act.feature.findlegislator.manager

import com.climatechangemakers.act.feature.findlegislator.model.GetLegislatorsRequest
import com.climatechangemakers.act.feature.findlegislator.model.GoogleCivicInformationResponse
import com.climatechangemakers.act.feature.findlegislator.model.GoogleCivicLegislator
import com.climatechangemakers.act.feature.findlegislator.model.Legislator
import com.climatechangemakers.act.feature.findlegislator.model.LegislatorRole
import com.climatechangemakers.act.feature.findlegislator.service.GoogleCivicInformationService
import com.climatechangemakers.act.feature.lcvscore.manager.LcvScoreManager
import com.climatechangemakers.act.feature.lcvscore.model.LcvScore
import com.climatechangemakers.act.feature.lcvscore.model.LcvScoreType
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class LegislatorsManager @Inject constructor(
  private val civicService: GoogleCivicInformationService,
  private val lcvScoreManager: LcvScoreManager,
) {

  suspend fun getLegislators(request: GetLegislatorsRequest): List<Legislator> = coroutineScope {
    val civicResponse = civicService.getLegislators(address = request.queryString)
    val roles = Array(civicResponse.legislators.size) { index ->
      civicResponse.offices.forEach { office ->
        if (index in office.legislatorIndices) return@Array office.role
      }
      error("No legislator role for index $index!")
    }

    civicResponse.legislators.mapIndexed { index, civicLegislator ->
      async {
        val lcvScores = lcvScoreManager.getScores(civicLegislator.name).also { scores ->
          // ensure we at least have a lifetime score for this individual
          checkNotNull(scores.firstOrNull { it.scoreType == LcvScoreType.LifetimeScore })
        }

        civicLegislator.toDomainLegislator(lcvScores, roles[index])
      }
    }.awaitAll()
  }
}

private val GetLegislatorsRequest.queryString: String get() = "$streetAddress, $city $state $postalCode"

private fun GoogleCivicLegislator.toDomainLegislator(lcvScores: List<LcvScore>, role: LegislatorRole) = Legislator(
  name = name,
  role = role,
  siteUrl = urls.first(),
  phone = phoneNumbers.first(),
  imageUrl = photoUrl,
  lcvScores = lcvScores,
)