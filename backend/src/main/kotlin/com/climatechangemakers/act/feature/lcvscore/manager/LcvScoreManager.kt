package com.climatechangemakers.act.feature.lcvscore.manager

import com.climatechangemakers.act.feature.lcvscore.model.LcvScore

fun interface LcvScoreManager {

  /**
   * Retrieve a list of [LcvScore] for a given representative's [bioguideId].
   * If no scores are found for this [bioguideId], an empty list is returned.
   */
  suspend fun getScores(bioguideId: String): List<LcvScore>
}