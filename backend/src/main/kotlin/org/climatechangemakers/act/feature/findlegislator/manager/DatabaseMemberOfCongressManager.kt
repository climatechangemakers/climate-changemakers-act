package org.climatechangemakers.act.feature.findlegislator.manager

import kotlinx.coroutines.withContext
import org.climatechangemakers.act.database.Database
import org.climatechangemakers.act.di.Io
import org.climatechangemakers.act.feature.findlegislator.model.MemberOfCongress
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class DatabaseMemberOfCongressManager @Inject constructor(
  database: Database,
  @Io private val ioDispatcher: CoroutineContext
) : MemberOfCongressManager {

  private val memberOfCongressQueries = database.memberOfCongressQueries

  override suspend fun getMemberOfCongressForBioguide(bioguideId: String) = withContext(ioDispatcher) {
    memberOfCongressQueries.selectForBioguide(bioguideId, ::MemberOfCongress).executeAsOne()
  }
}