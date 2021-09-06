package com.climatechangemakers.act.feature.action.manager

import com.climatechangemakers.act.database.Database
import com.climatechangemakers.act.di.Io
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class ActionTrackerManager @Inject constructor(
  database: Database,
  @Io private val ioDispatcher: CoroutineContext,
) {

  private val initiateActionQueries = database.actionInitiateQueries

  suspend fun trackActionInitiated(email: String) = withContext(ioDispatcher) {
    initiateActionQueries.insert(email)
  }
}