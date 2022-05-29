package org.climatechangemakers.act.di

import app.cash.sqldelight.ColumnAdapter
import org.climatechangemakers.act.database.Database
import app.cash.sqldelight.driver.jdbc.JdbcDriver
import app.cash.sqldelight.driver.jdbc.asJdbcDriver
import dagger.Module
import dagger.Provides
import org.climatechangemakers.act.common.columnadapter.StringEnumColumnAdapter
import org.climatechangemakers.act.database.Member_of_congress
import org.postgresql.ds.PGSimpleDataSource

@Module object DatabaseModule {

  @Provides fun providesClimateChangeMakersSqlDriver(): JdbcDriver = PGSimpleDataSource().apply {
    serverNames = arrayOf(getEnvironmentVariable(EnvironmentVariable.DatabaseHostname))
    portNumbers = intArrayOf(getEnvironmentVariable(EnvironmentVariable.DatabasePort).toInt())
    user = getEnvironmentVariable(EnvironmentVariable.DatabaseUser)
    password = getEnvironmentVariable(EnvironmentVariable.DatabasePassword)
    databaseName = getEnvironmentVariable(EnvironmentVariable.DatabaseName)
  }.asJdbcDriver()

  @Provides fun providesClimateChangeMakersDatabase(
    driver: JdbcDriver,
  ): Database = Database(
    driver = driver,
    member_of_congressAdapter = Member_of_congress.Adapter(
      stateAdapter = StringEnumColumnAdapter(),
      legislative_roleAdapter = StringEnumColumnAdapter(),
      partyAdapter = StringEnumColumnAdapter(),
    ),
  )
}