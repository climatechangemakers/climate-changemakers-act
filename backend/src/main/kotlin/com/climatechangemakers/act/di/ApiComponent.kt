package com.climatechangemakers.act.di

import com.climatechangemakers.act.feature.action.controller.ActionController
import dagger.Component

@Component(modules = [
  ServiceModule::class,
  DatabaseModule::class,
  CoroutineModule::class,
  BindingModule::class,
])
interface ApiComponent {

  fun actionController(): ActionController
}