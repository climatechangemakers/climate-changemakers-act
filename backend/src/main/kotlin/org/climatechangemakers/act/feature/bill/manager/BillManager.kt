package org.climatechangemakers.act.feature.bill.manager

import org.climatechangemakers.act.feature.bill.model.Bill

interface BillManager {

  /**
   * Get a list of [Bill] associated with this issue for the current
   * Congressional session.
   */
  suspend fun getBillsForIssueId(issueId: Long): List<Bill>
}