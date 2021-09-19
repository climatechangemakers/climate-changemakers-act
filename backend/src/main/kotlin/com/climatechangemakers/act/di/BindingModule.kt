package com.climatechangemakers.act.di

import com.climatechangemakers.act.feature.action.manager.ActionTrackerManager
import com.climatechangemakers.act.feature.action.manager.DatabaseActionTrackerManager
import com.climatechangemakers.act.feature.findlegislator.manager.DatabaseDistrictOfficeManager
import com.climatechangemakers.act.feature.findlegislator.manager.DistrictOfficerManager
import com.climatechangemakers.act.feature.issue.manager.DatabaseIssueManager
import com.climatechangemakers.act.feature.issue.manager.IssueManager
import com.climatechangemakers.act.feature.lcvscore.manager.DatabaseLcvScoreManager
import com.climatechangemakers.act.feature.lcvscore.manager.LcvScoreManager
import dagger.Binds
import dagger.Module

@Module interface BindingModule {

  @Binds fun bindsLcvScoreManager(manager: DatabaseLcvScoreManager): LcvScoreManager
  @Binds fun bindsIssueManager(manager: DatabaseIssueManager): IssueManager
  @Binds fun bindsActionTrackerManager(manager: DatabaseActionTrackerManager): ActionTrackerManager
  @Binds fun bindsDistrictOfficeManager(manager: DatabaseDistrictOfficeManager): DistrictOfficerManager
}