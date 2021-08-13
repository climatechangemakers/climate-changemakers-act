package com.climatechangemakers.act.di

import com.climatechangemakers.act.database.Database
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.asJdbcDriver
import dagger.Module
import dagger.Provides
import org.postgresql.ds.PGSimpleDataSource
import javax.inject.Named

@Module object DatabaseModule {

  @Provides @Named("database_host") fun providesHostname(): String = getEnvironmentVariable(
    EnvironmentVariable.DatabaseHostname
  )

  @Provides @Named("database_password") fun providesDatabasePassword(): String = getEnvironmentVariable(
    EnvironmentVariable.DatabasePassword
  )

  @Provides @Named("database_user") fun providesDatabaseUser(): String = getEnvironmentVariable(
    EnvironmentVariable.DatabaseUser
  )

  @Provides @Named("database_port") fun providesDatabasePort(): Int = getEnvironmentVariable(
    EnvironmentVariable.DatabasePort
  ).toInt()

  @Provides @Named("database_name") fun providesDatabaseName(): String = getEnvironmentVariable(
    EnvironmentVariable.DatabaseName
  )

  @Provides fun providesClimateChangeMakersSqlDriver(
    @Named("database_host") host: String,
    @Named("database_password") password: String,
    @Named("database_user") user: String,
    @Named("database_port") port: Int,
    @Named("database_name") databaseName: String,
  ): SqlDriver = PGSimpleDataSource().apply {
    serverNames = arrayOf(host)
    portNumbers = intArrayOf(port)
    setUser(user)
    setPassword(password)
    setDatabaseName(databaseName)
  }.asJdbcDriver()

  @Provides fun providesClimateChangeMakersDatabase(
    driver: SqlDriver,
  ): Database = Database(driver)
}