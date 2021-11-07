package org.climatechangemakers.act.feature.findlegislator.manager

import org.climatechangemakers.act.feature.findlegislator.model.GetLegislatorsByAddressRequest
import org.climatechangemakers.act.feature.findlegislator.model.Legislator
import org.climatechangemakers.act.feature.findlegislator.model.LegislatorArea
import org.climatechangemakers.act.feature.findlegislator.service.GeocodioService
import org.climatechangemakers.act.feature.lcvscore.manager.LcvScoreManager
import org.climatechangemakers.act.feature.lcvscore.model.LcvScore
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.climatechangemakers.act.feature.findlegislator.model.MemberOfCongress
import javax.inject.Inject

class LegislatorsManager @Inject constructor(
  private val geocodioService: GeocodioService,
  private val lcvScoreManager: LcvScoreManager,
  private val districtOfficerManager: DistrictOfficerManager,
  private val memberOfCongressManager: MemberOfCongressManager,
) {

  suspend fun getLegislators(request: GetLegislatorsByAddressRequest): List<Legislator> = coroutineScope {
    val geoCodioResponse = geocodioService.geocode(query = request.queryString).results.first()
    geoCodioResponse.fields.congressionalDistricts.first().currentLegislators.map { geocodioLegislator ->
      async {
        val bioguideId = geocodioLegislator.references.bioguide
        val lcvScores = async { getLcvScoresForBioguide(bioguideId) }
        val memberOfCongress = async { memberOfCongressManager.getMemberOfCongressForBioguide(bioguideId) }
        val districtPhoneNumber = async {
          districtOfficerManager.getNearestDistrictOfficePhoneNumber(bioguideId, geoCodioResponse.location)
        }

        memberOfCongress.await().toLegislator(
          districtPhoneNumber = districtPhoneNumber.await(),
          lcvScores = lcvScores.await(),
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

private fun MemberOfCongress.toLegislator(
  districtPhoneNumber: String?,
  lcvScores: List<LcvScore>,
) = Legislator(
  name = fullName,
  bioguideId = bioguideId,
  role = legislativeRole,
  phoneNumbers = listOfNotNull(dcPhoneNumber, districtPhoneNumber),
  imageUrl = imageUrl,
  twitter = twitterHandle,
  area = LegislatorArea(state = representedArea, congressionalDistrict),
  partyAffiliation = party,
  lcvScores = lcvScores,
)

private val MemberOfCongress.imageUrl: String get() {
  return "https://bioguide.congress.gov/bioguide/photo/${bioguideId[0]}/$bioguideId.jpg"
}
