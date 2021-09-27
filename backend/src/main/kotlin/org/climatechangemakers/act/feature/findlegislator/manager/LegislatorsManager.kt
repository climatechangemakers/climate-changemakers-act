package org.climatechangemakers.act.feature.findlegislator.manager

import org.climatechangemakers.act.feature.findlegislator.model.GeocodioLegislator
import org.climatechangemakers.act.feature.findlegislator.model.GetLegislatorsByAddressRequest
import org.climatechangemakers.act.feature.findlegislator.model.Legislator
import org.climatechangemakers.act.feature.findlegislator.model.LegislatorArea
import org.climatechangemakers.act.feature.findlegislator.model.LegislatorRole
import org.climatechangemakers.act.feature.findlegislator.model.domainPoliticalParty
import org.climatechangemakers.act.feature.findlegislator.service.GeocodioService
import org.climatechangemakers.act.feature.lcvscore.manager.LcvScoreManager
import org.climatechangemakers.act.feature.lcvscore.model.LcvScore
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.climatechangemakers.act.common.model.RepresentedArea
import javax.inject.Inject

class LegislatorsManager @Inject constructor(
  private val geocodioService: GeocodioService,
  private val lcvScoreManager: LcvScoreManager,
  private val districtOfficerManager: DistrictOfficerManager,
) {

  suspend fun getLegislators(request: GetLegislatorsByAddressRequest): List<Legislator> = coroutineScope {
    val geoCodioResponse = geocodioService.geocode(query = request.queryString).results.first()
    val congressionalDistrict = geoCodioResponse.fields.congressionalDistricts.first()

    congressionalDistrict.currentLegislators.map { geocodioLegislator ->
      async {
        val bioguideId = geocodioLegislator.references.bioguide
        val lcvScores = async { getLcvScoresForBioguide(bioguideId) }
        val districtPhoneNumber = async {
          districtOfficerManager.getNearestDistrictOfficePhoneNumber(bioguideId, geoCodioResponse.location)
        }

        geocodioLegislator.toDomainLegislator(
          state = request.state,
          districtNumber = congressionalDistrict.districtNumber,
          lcvScores = lcvScores.await(),
          districtPhoneNumber = districtPhoneNumber.await(),
        )
      }
    }.awaitAll()
  }

  @OptIn(ExperimentalStdlibApi::class)
  private suspend fun getLcvScoresForBioguide(bioguideId: String): List<LcvScore> = coroutineScope {
    val lifetimeScore = async { lcvScoreManager.getLifetimeScore(bioguideId) }
    val yearlyScores = async { lcvScoreManager.getYearlyScores(bioguideId) }

    buildList {
      lifetimeScore.await()?.let(::add)
      addAll(yearlyScores.await())
    }
  }
}

private val GetLegislatorsByAddressRequest.queryString: String get() = "$streetAddress, $city $state $postalCode"

private val GeocodioLegislator.fullName get() = "${bio.firstName} ${bio.lastName}"

private fun GeocodioLegislator.toDomainLegislator(
  state: RepresentedArea,
  districtNumber: Int,
  districtPhoneNumber: String?,
  lcvScores: List<LcvScore>,
) = Legislator(
  name = fullName,
  bioguideId = references.bioguide,
  role = type,
  siteUrl = contactInfo.siteUrl,
  phoneNumbers = listOfNotNull(contactInfo.phone, districtPhoneNumber),
  imageUrl = imageUrl,
  partyAffiliation = bio.party.domainPoliticalParty,
  area = LegislatorArea(state = state, if (type == LegislatorRole.Senator) null else districtNumber),
  lcvScores = lcvScores,
)

private val GeocodioLegislator.imageUrl: String get() {
  val bioguide = references.bioguide
  return "https://bioguide.congress.gov/bioguide/photo/${bioguide[0]}/$bioguide.jpg"
}
