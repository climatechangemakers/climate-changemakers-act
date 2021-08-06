package com.climatechangemakers.act.feature.lcvscore.manager

import com.climatechangemakers.act.db.Database
import com.climatechangemakers.act.feature.lcvscore.model.LcvScore
import com.climatechangemakers.act.feature.lcvscore.model.LcvScoreType
import com.squareup.sqldelight.sqlite.driver.JdbcDriver
import kotlinx.coroutines.runBlocking
import java.sql.Connection
import java.sql.DriverManager
import kotlin.test.Test
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

class RealLcvScoreManagerTest {

  private val connection = DriverManager.getConnection("jdbc:tc:postgresql:12.5:///my_db")
  private val driver = object : JdbcDriver() {
    override fun closeConnection(connection: Connection) = Unit
    override fun getConnection(): Connection = connection
  }

  private val database = Database(driver)
  private val manager = RealLcvScoreManager(database, EmptyCoroutineContext)

  @BeforeTest fun before() {
    Database.Schema.create(driver)
  }

  @AfterTest fun after() = connection.close()

  @Test fun `select by id returns correct values`() = runBlocking {
    insert("foo", LcvScore(99, LcvScoreType.LifetimeScore))
    insert("foo", LcvScore(88, LcvScoreType.YearlyScore(2020)))
    insert("bar", LcvScore(1, LcvScoreType.LifetimeScore))

    val result = manager.getScores("foo")

    assertEquals(
      listOf(LcvScore(99, LcvScoreType.LifetimeScore), LcvScore(88, LcvScoreType.YearlyScore(2020))),
      result
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