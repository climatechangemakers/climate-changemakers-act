package com.climatechangemakers.act.di

import com.climatechangemakers.act.feature.lcvscore.manager.LcvScoreManager
import com.climatechangemakers.act.feature.lcvscore.manager.RealLcvScoreManager
import dagger.Binds
import dagger.Module

@Module interface BindingModule {

  @Binds fun bindsLcvScoreManager(manager: RealLcvScoreManager): LcvScoreManager
}