package org.climatechangemakers.act.feature.membership.manager

import org.climatechangemakers.act.common.model.RepresentedArea
import org.climatechangemakers.act.common.util.withRetry
import org.climatechangemakers.act.feature.action.manager.ActionTrackerManager
import org.climatechangemakers.act.feature.membership.model.AirtableCreateRecordRequest
import org.climatechangemakers.act.feature.membership.service.AirtableFormula
import org.climatechangemakers.act.feature.membership.service.AirtableService
import javax.inject.Inject

class AirtableMembershipManager @Inject constructor(
  private val actionTrackerManager: ActionTrackerManager,
  private val airtableService: AirtableService,
) : MembershipManager {

  override suspend fun checkMembership(email: String): Boolean = withRetry(3) {
    airtableService.checkMembership(formula = AirtableFormula.FilterByEmailFormula(email))
  }.records.isNotEmpty()

  override suspend fun signUp(
    email: String,
    firstName: String,
    lastName: String,
    city: String,
    state: RepresentedArea,
    experience: Boolean,
    referral: String,
    actionReason: String,
    socialVerification: String,
  ) {
    val airtableRequest = AirtableCreateRecordRequest(
      email,
      firstName,
      lastName,
      city,
      state,
      experience,
      referral,
      actionReason,
      socialVerification,
    )

    withRetry(3) {
      airtableService.signUp(airtableRequest)
    }

    // TODO(kcianfarini) send mailchimp welcome email

    actionTrackerManager.trackActionSignUp(email)
  }
}