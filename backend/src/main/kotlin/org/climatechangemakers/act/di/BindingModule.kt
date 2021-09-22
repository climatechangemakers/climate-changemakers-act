package org.climatechangemakers.act.di

import org.climatechangemakers.act.feature.action.manager.ActionTrackerManager
import org.climatechangemakers.act.feature.action.manager.DatabaseActionTrackerManager
import org.climatechangemakers.act.feature.findlegislator.manager.DatabaseDistrictOfficeManager
import org.climatechangemakers.act.feature.findlegislator.manager.DistrictOfficerManager
import org.climatechangemakers.act.feature.issue.manager.DatabaseIssueManager
import org.climatechangemakers.act.feature.issue.manager.IssueManager
import org.climatechangemakers.act.feature.lcvscore.manager.DatabaseLcvScoreManager
import org.climatechangemakers.act.feature.lcvscore.manager.LcvScoreManager
import dagger.Binds
import dagger.Module

@Module interface BindingModule {

  @Binds fun bindsLcvScoreManager(manager: DatabaseLcvScoreManager): LcvScoreManager
  @Binds fun bindsIssueManager(manager: DatabaseIssueManager): IssueManager
  @Binds fun bindsActionTrackerManager(manager: DatabaseActionTrackerManager): ActionTrackerManager
  @Binds fun bindsDistrictOfficeManager(manager: DatabaseDistrictOfficeManager): DistrictOfficerManager
}