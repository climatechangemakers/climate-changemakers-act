package com.climatechangemakers.act.di

import com.climatechangemakers.act.feature.representativefinder.controller.RepresentativeController
import com.climatechangemakers.act.feature.representativefinder.manager.LegislatorFinderManager
import dagger.Component

@Component(modules = [ServiceModule::class])
interface ApiComponent {

  fun representativesFinderController(): RepresentativeController
}