package com.climatechangemakers.act.feature.congressgov.manager

fun interface SearchCongressManager {

  suspend fun getLegislatorImage(): String
}