package com.climatechangemakers.act.feature.lcvscore.manager

import com.climatechangemakers.act.database.Database
import com.climatechangemakers.act.di.Io
import com.climatechangemakers.act.feature.lcvscore.model.LcvScore
import com.climatechangemakers.act.feature.lcvscore.model.LcvScoreType
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named
import kotlin.coroutines.CoroutineContext

class DatabaseLcvScoreManager @Inject constructor(
  database: Database,
  @Io private val coroutineContext: CoroutineContext,
) : LcvScoreManager {

  private val lifetimeQueries = database.lcvLifetimeScoreQueries
  private val yearQueries = database.lcvYearScoreQueries

  override suspend fun getLifetimeScore(bioguideId: String): LcvScore? = withContext(coroutineContext) {
    lifetimeQueries.selectById(bioguideId)
      .executeAsOneOrNull()
      ?.let { score -> LcvScore(score, LcvScoreType.LifetimeScore) }
  }

  override suspend fun getYearlyScores(bioguideId: String): List<LcvScore> = withContext(coroutineContext) {
    yearQueries.selectById(bioguideId) { scoreYear, score ->
      LcvScore(score, LcvScoreType.YearlyScore(scoreYear))
    }.executeAsList()
  }
}