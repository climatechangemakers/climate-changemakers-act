package org.climatechangemakers.act.feature.email.manager

import kotlinx.coroutines.channels.Channel
import org.climatechangemakers.act.common.model.RepresentedArea

class FakeEmailEnrollmentManager : EmailEnrollmentManager {

  val emails = Channel<String>(Channel.UNLIMITED)
  val firstNames = Channel<String>(Channel.UNLIMITED)
  val lastNames = Channel<String>(Channel.UNLIMITED)
  val states = Channel<RepresentedArea>(Channel.UNLIMITED)

  override suspend fun subscribeChangemaker(
    email: String,
    firstName: String,
    lastName: String,
    state: RepresentedArea
  ) {
    emails.trySend(email)
    firstNames.trySend(firstName)
    lastNames.trySend(lastName)
    states.trySend(state)
  }

  override suspend fun subscribeChangemaker(email: String) {
    emails.trySend(email)
  }
}