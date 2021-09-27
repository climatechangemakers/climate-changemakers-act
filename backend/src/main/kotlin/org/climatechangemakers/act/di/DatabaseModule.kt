package org.climatechangemakers.act.di

import org.climatechangemakers.act.database.Database
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.asJdbcDriver
import dagger.Module
import dagger.Provides
import org.climatechangemakers.act.common.columnadapter.StringEnumColumnAdapter
import org.climatechangemakers.act.database.Member_of_congress
import org.postgresql.ds.PGSimpleDataSource

@Module object DatabaseModule {

  @Provides fun providesClimateChangeMakersSqlDriver(): SqlDriver = PGSimpleDataSource().apply {
    serverNames = arrayOf(getEnvironmentVariable(EnvironmentVariable.DatabaseHostname))
    portNumbers = intArrayOf(getEnvironmentVariable(EnvironmentVariable.DatabasePort).toInt())
    user = getEnvironmentVariable(EnvironmentVariable.DatabaseUser)
    password = getEnvironmentVariable(EnvironmentVariable.DatabasePassword)
    databaseName = getEnvironmentVariable(EnvironmentVariable.DatabaseName)
  }.asJdbcDriver()

  @Provides fun providesClimateChangeMakersDatabase(
    driver: SqlDriver,
  ): Database = Database(
    driver = driver,
    member_of_congressAdapter = Member_of_congress.Adapter(
      stateAdapter = StringEnumColumnAdapter(),
      legislative_roleAdapter = StringEnumColumnAdapter(),
      partyAdapter = StringEnumColumnAdapter(),
    ),
  )
}