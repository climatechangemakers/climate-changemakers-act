package org.climatechangemakers.act.feature.util

import org.climatechangemakers.act.database.Database
import com.squareup.sqldelight.sqlite.driver.JdbcDriver
import java.sql.Connection
import java.sql.DriverManager
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

abstract class TestContainerProvider {

  // the jdbc url is special and instructs testcontainers to use the postgres 12.5 image
  private val connection = DriverManager.getConnection("jdbc:tc:postgresql:12.5:///my_db")
  protected val driver = object : JdbcDriver() {
    override fun closeConnection(connection: Connection) = Unit
    override fun getConnection(): Connection = connection
  }

  protected val database = Database(driver)

  @BeforeTest fun before() = Database.Schema.create(driver)
  @AfterTest fun after() = connection.close()
}