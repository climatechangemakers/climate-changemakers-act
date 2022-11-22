package org.climatechangemakers.act.di

import org.climatechangemakers.act.feature.action.controller.ActionController
import org.climatechangemakers.act.feature.issue.controller.IssueListController
import dagger.Component
import kotlinx.serialization.json.Json
import org.climatechangemakers.act.feature.cms.controller.ContentManagementBillController
import org.climatechangemakers.act.feature.cms.controller.ContentManagementIssueController
import org.climatechangemakers.act.feature.cms.controller.ContentManagementTalkingPointsController
import org.climatechangemakers.act.feature.cms.manager.auth.UserVerificationManager
import org.climatechangemakers.act.feature.values.controller.ValuesController
import javax.inject.Singleton

@Singleton
@Component(modules = [
  BindingModule::class,
  CoroutineModule::class,
  DatabaseModule::class,
  LoggerModule::class,
  ServiceModule::class,
  SerializationModule::class,
  ClockModule::class,
])
interface ApiComponent {

  fun actionController(): ActionController
  fun issueController(): IssueListController
  fun valuesController(): ValuesController
  fun json(): Json
  fun userVerificationManager(): UserVerificationManager
  fun billController(): ContentManagementBillController
  fun cmsIssueController(): ContentManagementIssueController
  fun cmsTalkingPointsController(): ContentManagementTalkingPointsController
}