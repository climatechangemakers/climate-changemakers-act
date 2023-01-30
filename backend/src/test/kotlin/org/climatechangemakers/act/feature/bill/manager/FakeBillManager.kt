package org.climatechangemakers.act.feature.bill.manager

import org.climatechangemakers.act.feature.bill.model.Bill


class FakeBillManager(private val getBills: (Long) -> List<Bill>) : BillManager {

  override suspend fun getBillsForIssueId(issueId: Long): List<Bill> {
    return getBills(issueId)
  }
}