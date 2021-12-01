package org.climatechangemakers.act.feature.membership.manager

import org.climatechangemakers.act.common.model.RepresentedArea

interface MembershipManager {

  /**
   * Check if the Changemaker aliased by [email] is already
   * signed up for our community.
   */
  suspend fun checkMembership(email: String): Boolean

  /**
   * Sign a new member up for the Climate Changemakers community. This records their membership in some
   * datasource, and sends them a welcome email.
   */
  suspend fun signUp(
    email: String,
    firstName: String,
    lastName: String,
    city: String,
    state: RepresentedArea,
    experience: Boolean,
    referral: String,
  )
}