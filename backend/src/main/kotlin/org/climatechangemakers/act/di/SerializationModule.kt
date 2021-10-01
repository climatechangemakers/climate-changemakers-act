package org.climatechangemakers.act.di

import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json

@Module object SerializationModule {

  @Provides fun providesJson(): Json = Json {
    prettyPrint = true
  }
}