package org.climatechangemakers.act.di

import org.climatechangemakers.act.feature.action.controller.ActionController
import org.climatechangemakers.act.feature.issue.controller.IssueListController
import dagger.Component

@Component(modules = [
  ServiceModule::class,
  DatabaseModule::class,
  CoroutineModule::class,
  BindingModule::class,
])
interface ApiComponent {

  fun actionController(): ActionController
  fun issueController(): IssueListController
}