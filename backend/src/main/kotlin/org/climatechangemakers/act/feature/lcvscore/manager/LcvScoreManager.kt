package org.climatechangemakers.act.feature.lcvscore.manager

import org.climatechangemakers.act.feature.lcvscore.model.LcvScore

interface LcvScoreManager {

  /**
   * Retrieve the cumulative [LcvScore] for a given represenative's [bioguideId]. If no score exists, null is retuend.
   */
  suspend fun getLifetimeScore(bioguideId: String): LcvScore?

  /**
   * Retrieve a list of yearly [LcvScore] for a given representative's [bioguideId]. The returned list will be ordered
   * by year descending. If no scores are found for this [bioguideId], an empty list is returned.
   */
  suspend fun getYearlyScores(bioguideId: String): List<LcvScore>
}