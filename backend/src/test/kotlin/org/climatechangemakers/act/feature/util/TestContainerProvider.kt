package org.climatechangemakers.act.feature.util

import app.cash.sqldelight.Query
import org.climatechangemakers.act.database.Database
import app.cash.sqldelight.driver.jdbc.JdbcDriver
import org.climatechangemakers.act.common.columnadapter.StringEnumColumnAdapter
import org.climatechangemakers.act.database.Congress_bill
import org.climatechangemakers.act.database.Member_of_congress
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
    override fun notifyListeners(queryKeys: Array<String>) = Unit
    override fun removeListener(listener: Query.Listener, queryKeys: Array<String>) = Unit
    override fun addListener(listener: Query.Listener, queryKeys: Array<String>) = Unit
  }

  protected val database = Database(
    driver = driver,
    member_of_congressAdapter = Member_of_congress.Adapter(
      stateAdapter = StringEnumColumnAdapter(),
      legislative_roleAdapter = StringEnumColumnAdapter(),
      partyAdapter = StringEnumColumnAdapter(),
    ),
    congress_billAdapter = Congress_bill.Adapter(
      bill_typeAdapter = StringEnumColumnAdapter(),
    )
  )

  @BeforeTest fun before() {
    driver.execute(0, "CREATE EXTENSION IF NOT EXISTS citext;", 0)
    Database.Schema.create(driver)
  }

  @AfterTest fun after() = connection.close()
}