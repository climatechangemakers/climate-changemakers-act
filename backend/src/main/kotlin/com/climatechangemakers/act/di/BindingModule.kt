package com.climatechangemakers.act.di

import com.climatechangemakers.act.feature.issue.manager.FakeIssueManager
import com.climatechangemakers.act.feature.issue.manager.IssueManager
import com.climatechangemakers.act.feature.lcvscore.manager.DatabaseLcvScoreManager
import com.climatechangemakers.act.feature.lcvscore.manager.LcvScoreManager
import dagger.Binds
import dagger.Module

@Module interface BindingModule {

  @Binds fun bindsLcvScoreManager(manager: DatabaseLcvScoreManager): LcvScoreManager
  @Binds fun bindsIssueManager(manager: FakeIssueManager): IssueManager
}