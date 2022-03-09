package org.climatechangemakers.act.feature.email.manager

import org.climatechangemakers.act.common.model.RepresentedArea

interface EmailEnrollmentManager {

  /**
   * Signs a Changemaker up for promotional emails with a fully qualified list of information. This is used when
   * someone decides to enroll in the Climate Changemaker community.
   */
  suspend fun subscribeChangemaker(
    email: String,
    firstName: String,
    lastName: String,
    state: RepresentedArea,
  )

  /**
   * Signs a Changemaker up for promotional emails with only their email. This is used when
   * someone desires just the newsletter, and not to be part of our community.
   *
   * @return if the changemaker was signed up
   */
  suspend fun subscribeChangemaker(email: String): Boolean
}