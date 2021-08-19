package com.climatechangemakers.act.di

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers
import javax.inject.Named
import kotlin.coroutines.CoroutineContext

@Module object CoroutineModule {

  @Provides @Io fun providesIoDispatcher(): CoroutineContext = Dispatchers.IO
}