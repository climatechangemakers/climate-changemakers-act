package org.climatechangemakers.act.feature.membership.manager

import org.climatechangemakers.act.feature.action.manager.ActionTrackerManager
import javax.inject.Inject

class AirtableMembershipManager @Inject constructor(
  private val actionTrackerManager: ActionTrackerManager,
) : MembershipManager {

  override suspend fun signUp(email: String) {
    // TODO(kcianfarini) implement sign up
    actionTrackerManager.trackActionSignUp(email)
  }
}