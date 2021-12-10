package org.climatechangemakers.act.feature.email.manager

import org.climatechangemakers.act.common.model.RepresentedArea

interface EmailEnrollmentManager {

  suspend fun subscribeChangemaker(
    email: String,
    firstName: String,
    lastName: String,
    state: RepresentedArea,
  )
}