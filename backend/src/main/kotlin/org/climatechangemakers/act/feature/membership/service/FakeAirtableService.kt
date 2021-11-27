package org.climatechangemakers.act.feature.membership.service

import org.climatechangemakers.act.feature.membership.model.AirtableRecord
import org.climatechangemakers.act.feature.membership.model.AirtableResponse

/**
 * A fake [AirtableService] that can be used for testing and local development.
 */
class FakeAirtableService : AirtableService {

  /**
   * Map of record email to record ID.
   */
  private val registeredMembers = mutableMapOf("foo@bar.com" to "someid")

  override suspend fun checkMembership(formula: AirtableFormula.FilterByEmailFormula) = AirtableResponse(
    records = registeredMembers[formula.email]?.let { id -> listOf(AirtableRecord(id)) } ?: emptyList()
  )
}