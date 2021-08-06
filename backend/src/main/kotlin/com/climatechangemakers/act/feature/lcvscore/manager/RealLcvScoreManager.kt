package com.climatechangemakers.act.feature.lcvscore.manager

import com.climatechangemakers.act.db.Database
import com.climatechangemakers.act.feature.lcvscore.model.LcvScore
import com.climatechangemakers.act.feature.lcvscore.model.LcvScoreType
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named
import kotlin.coroutines.CoroutineContext

class RealLcvScoreManager @Inject constructor(
  database: Database,
  @Named("IO") private val coroutineContext: CoroutineContext,
) : LcvScoreManager {

  private val lifetimeQueries = database.lcvLifetimeScoreQueries
  private val yearQueries = database.lcvYearScoreQueries

  @OptIn(ExperimentalStdlibApi::class)
  override suspend fun getScores(bioguideId: String): List<LcvScore> = withContext(coroutineContext) {
    val lifetimeScore = async {
      lifetimeQueries.selectById(bioguideId)
        .executeAsOneOrNull()
        ?.let { score -> LcvScore(score, LcvScoreType.LifetimeScore) }
    }

    val yearlyScores = async {
      yearQueries.selectById(bioguideId) { scoreYear, score ->
        LcvScore(score, LcvScoreType.YearlyScore(scoreYear))
      }.executeAsList()
    }

    buildList {
      lifetimeScore.await()?.run(::add)
      addAll(yearlyScores.await())
    }
  }
}