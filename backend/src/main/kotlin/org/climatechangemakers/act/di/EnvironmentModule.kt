package org.climatechangemakers.act.di

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module object EnvironmentModule {

  @Provides
  @Singleton
  @IsProduction
  fun providesIsProduction() = getEnvironmentVariable(EnvironmentVariable.IsProduction).toBooleanStrict()
}