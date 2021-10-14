package org.climatechangemakers.act.di

import org.climatechangemakers.act.feature.action.controller.ActionController
import org.climatechangemakers.act.feature.issue.controller.IssueListController
import dagger.Component
import kotlinx.serialization.json.Json
import org.climatechangemakers.act.feature.values.controller.ValuesController

@Component(modules = [
  BindingModule::class,
  CoroutineModule::class,
  DatabaseModule::class,
  LoggerModule::class,
  ServiceModule::class,
  SerializationModule::class,
])
interface ApiComponent {

  fun actionController(): ActionController
  fun issueController(): IssueListController
  fun valuesController(): ValuesController
  fun json(): Json
}