package org.climatechangemakers.act.feature.lcvscore.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable data class LcvScore(
  val score: Int,
  val scoreType: LcvScoreType,
)

@Serializable sealed class LcvScoreType {
  @Serializable @SerialName("lifetime") object LifetimeScore : LcvScoreType()
  @Serializable @SerialName("year") data class YearlyScore(val year: Int) : LcvScoreType()
}