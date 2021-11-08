package org.climatechangemakers.act.feature.membership.manager

interface MembershipManager {

  suspend fun signUp(email: String)
}