package com.climatechangemakers.act.feature.lcvscore.manager

import com.climatechangemakers.act.feature.util.TestContainerProvider
import com.climatechangemakers.act.feature.lcvscore.model.LcvScore
import com.climatechangemakers.act.feature.lcvscore.model.LcvScoreType
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.assertEquals

class DatabaseLcvScoreManagerTest : TestContainerProvider() {

  private val manager = DatabaseLcvScoreManager(database, EmptyCoroutineContext)

  @Test fun `select lifetime by id returns correct value`() = runBlocking {
    insert("foo", LcvScore(99, LcvScoreType.LifetimeScore))
    insert("foo", LcvScore(88, LcvScoreType.YearlyScore(2020)))
    insert("bar", LcvScore(1, LcvScoreType.LifetimeScore))

    assertEquals(
      LcvScore(99, LcvScoreType.LifetimeScore),
      manager.getLifetimeScore("foo")
    )
  }

  @Test fun `select yearly by id returns correct values in correct order`() = runBlocking {
    insert("foo", LcvScore(99, LcvScoreType.LifetimeScore))
    insert("foo", LcvScore(65, LcvScoreType.YearlyScore(2018)))
    insert("foo", LcvScore(70, LcvScoreType.YearlyScore(2019)))
    insert("foo", LcvScore(88, LcvScoreType.YearlyScore(2020)))
    insert("bar", LcvScore(1, LcvScoreType.LifetimeScore))

    assertEquals(
      listOf(
        LcvScore(88, LcvScoreType.YearlyScore(2020)),
        LcvScore(70, LcvScoreType.YearlyScore(2019)),
        LcvScore(65, LcvScoreType.YearlyScore(2018)),
      ),
      manager.getYearlyScores("foo")
    )
  }

  private fun insert(id: String, score: LcvScore) = when (val type = score.scoreType) {
    LcvScoreType.LifetimeScore -> {
      driver.execute(0, "INSERT INTO lcv_score_lifetime VALUES (?, ?)", 2) {
        bindString(1, id)
        bindLong(2, score.score.toLong())
      }
    }
    is LcvScoreType.YearlyScore -> {
      driver.execute(0, "INSERT INTO lcv_score_year VALUES (?, ?, ?)", 3) {
        bindString(1, id)
        bindLong(2, type.year.toLong())
        bindLong(3, score.score.toLong())
      }
    }
  }
}