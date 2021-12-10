package org.climatechangemakers.act.feature.membership.manager

import org.climatechangemakers.act.common.model.RepresentedArea
import org.climatechangemakers.act.common.util.withRetry
import org.climatechangemakers.act.feature.action.manager.ActionTrackerManager
import org.climatechangemakers.act.feature.email.manager.EmailEnrollmentManager
import org.climatechangemakers.act.feature.membership.model.AirtableCreateRecordRequest
import org.climatechangemakers.act.feature.membership.service.AirtableFormula
import org.climatechangemakers.act.feature.membership.service.AirtableService
import javax.inject.Inject

class AirtableMembershipManager @Inject constructor(
  private val actionTrackerManager: ActionTrackerManager,
  private val airtableService: AirtableService,
  private val emailEnrollmentManager: EmailEnrollmentManager,
) : MembershipManager {

  override suspend fun checkMembership(email: String): Boolean = withRetry(3) {
    airtableService.checkMembership(formula = AirtableFormula.FilterByEmailFormula(email))
  }.records.isNotEmpty()

  override suspend fun signUp(
    email: String,
    firstName: String,
    lastName: String,
    postalCode: String,
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
      postalCode,
      state,
      experience,
      referral,
      actionReason,
      socialVerification,
    )

    withRetry(3) { airtableService.signUp(airtableRequest) }
    emailEnrollmentManager.subscribeChangemaker(email, firstName, lastName, state)
    actionTrackerManager.trackActionSignUp(email)
  }
}