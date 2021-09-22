package org.climatechangemakers.act.feature.findlegislator.manager

import org.climatechangemakers.act.database.Database
import org.climatechangemakers.act.di.Io
import org.climatechangemakers.act.feature.findlegislator.model.Location
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class DatabaseDistrictOfficeManager @Inject constructor(
  database: Database,
  @Io private val ioDispatcher: CoroutineContext,
) : DistrictOfficerManager {

  private val districtOfficeQueries = database.districtOfficeQueries

  override suspend fun getNearestDistrictOfficePhoneNumber(
    bioguideId: String,
    requestingLocation: Location
  ): String? = withContext(ioDispatcher) {
    districtOfficeQueries.selectClosestDistrictOfficePhoneNumber(
      bioguideId = bioguideId,
      lat = requestingLocation.lat,
      long = requestingLocation.long
    ).executeAsOneOrNull()
  }
}