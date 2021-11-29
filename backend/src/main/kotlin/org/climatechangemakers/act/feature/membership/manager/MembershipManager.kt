package org.climatechangemakers.act.feature.membership.manager

interface MembershipManager {

  /**
   * Check if the Changemaker aliased by [email] is already
   * signed up for our community.
   */
  suspend fun checkMembership(email: String): Boolean

  suspend fun signUp(email: String)
}