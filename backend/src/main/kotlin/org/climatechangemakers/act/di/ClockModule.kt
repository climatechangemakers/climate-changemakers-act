package org.climatechangemakers.act.di

import dagger.Module
import dagger.Provides
import kotlinx.datetime.Clock

@Module object ClockModule {

  @Provides fun providesClock(): Clock = Clock.System
}