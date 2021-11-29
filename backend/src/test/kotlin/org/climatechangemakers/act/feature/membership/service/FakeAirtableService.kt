package org.climatechangemakers.act.feature.membership.service

import org.climatechangemakers.act.feature.membership.model.AirtableRecord
import org.climatechangemakers.act.feature.membership.model.AirtableResponse
import retrofit2.Response

/**
 * A fake [AirtableService] that can be used for testing and local development.
 */
class FakeAirtableService : AirtableService {

  /**
   * Map of record email to record ID.
   */
  val registeredMembers = mutableSetOf<String>()

  override suspend fun checkMembership(
    formula: AirtableFormula.FilterByEmailFormula
  ): Response<AirtableResponse> = Response.success(
    AirtableResponse(
      records = if (formula.email in registeredMembers) listOf(AirtableRecord(formula.email)) else emptyList()
    )
  )
}