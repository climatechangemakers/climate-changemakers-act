package com.climatechangemakers.act.feature.lcvscore.manager

import com.climatechangemakers.act.feature.lcvscore.model.LcvScore
import com.climatechangemakers.act.feature.lcvscore.model.LcvScoreType
import javax.inject.Inject

class RealLcvScoreManager @Inject constructor() : LcvScoreManager {

  override suspend fun getScores(legislatorName: String): List<LcvScore> {
    // TODO(kcianfarini) back this by a real service or
    // a database query
    return listOf(
      LcvScore((0..100).random(), LcvScoreType.LifetimeScore),
      LcvScore((0..100).random(), LcvScoreType.YearlyScore(2020)),
      LcvScore((0..100).random(), LcvScoreType.YearlyScore(2019)),
    )
  }
}