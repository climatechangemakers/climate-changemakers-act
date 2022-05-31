package org.climatechangemakers.act.feature.findlegislator.manager

import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.todayAt
import org.climatechangemakers.act.common.model.RepresentedArea
import org.climatechangemakers.act.database.Database
import org.climatechangemakers.act.di.Io
import org.climatechangemakers.act.feature.findlegislator.model.MemberOfCongress
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class DatabaseMemberOfCongressManager @Inject constructor(
  database: Database,
  private val clock: Clock,
  @Io private val ioDispatcher: CoroutineContext,
) : MemberOfCongressManager {

  private val memberOfCongressQueries = database.memberOfCongressQueries

  override suspend fun getMemberOfCongressForBioguide(bioguideId: String) = withContext(ioDispatcher) {
    memberOfCongressQueries.selectForBioguide(bioguideId, ::MemberOfCongress).executeAsOne()
  }

  override suspend fun getMembersForCongressionalDistrict(
    state: RepresentedArea,
    district: Short,
  ): List<MemberOfCongress> = withContext(ioDispatcher) {
    memberOfCongressQueries.selectForCongressionalDistrict(
      state = state,
      congressionalDistrict = district,
      today = clock.todayAt(TimeZone.UTC).toJavaLocalDate(),
      mapper = ::MemberOfCongress,
    ).executeAsList()
  }

  override suspend fun getTwitterHandlesForBioguides(
    bioguides: List<String>,
  ): List<String> = withContext(ioDispatcher) {
    memberOfCongressQueries.selectTwitterHandlesForBioguides(bioguides).executeAsList()
  }
}