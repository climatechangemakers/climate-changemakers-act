package com.climatechangemakers.act.di

import com.climatechangemakers.act.feature.congressgov.manager.RealSearchCongressManager
import com.climatechangemakers.act.feature.congressgov.manager.SearchCongressManager
import com.climatechangemakers.act.feature.lcvscore.manager.LcvScoreManager
import com.climatechangemakers.act.feature.lcvscore.manager.RealLcvScoreManager
import dagger.Binds
import dagger.Module

@Module interface BindingModule {

  @Binds fun bindsLcvScoreManager(manager: RealLcvScoreManager): LcvScoreManager
  @Binds fun bindsSearchCongressManagaer(manager: RealSearchCongressManager): SearchCongressManager
}