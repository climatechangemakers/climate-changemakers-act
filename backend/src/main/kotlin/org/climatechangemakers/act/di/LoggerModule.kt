package org.climatechangemakers.act.di

import dagger.Module
import dagger.Provides
import org.slf4j.Logger

@Module class LoggerModule(private val logger: Logger) {

  @Provides fun providesLogger(): Logger = logger
}