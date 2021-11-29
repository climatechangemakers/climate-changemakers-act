package org.climatechangemakers.act.feature.membership.manager

import org.climatechangemakers.act.common.util.withRetry
import org.climatechangemakers.act.feature.action.manager.ActionTrackerManager
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

  override suspend fun signUp(email: String) {
    // TODO(kcianfarini) implement sign up
    actionTrackerManager.trackActionSignUp(email)
  }
}