package com.climatechangemakers.act.feature.lcvscore.manager

import com.climatechangemakers.act.feature.lcvscore.model.LcvScore

fun interface LcvScoreManager {

  suspend fun getScores(legislatorName: String): List<LcvScore>
}