package com.climatechangemakers.act.feature.lcvscore.manager

import javax.inject.Inject

class RealLcvScoreManager @Inject constructor() : LcvScoreManager {

  override suspend fun getScore(legislatorName: String): Int {
    // TODO(kcianfarini) back this by a real service or
    // a database query
    return (0..100).random()
  }
}