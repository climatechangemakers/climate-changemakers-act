package com.climatechangemakers.act.feature.lcvscore.manager

fun interface LcvScoreManager {

  suspend fun getScore(legislatorName: String): Int
}