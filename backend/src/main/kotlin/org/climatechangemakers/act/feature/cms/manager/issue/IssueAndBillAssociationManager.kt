package org.climatechangemakers.act.feature.cms.manager.issue

import org.climatechangemakers.act.feature.bill.model.Bill

interface IssueAndBillAssociationManager {

  suspend fun getBillsForIssueId(issueId: Long): List<Bill>

  suspend fun associateBillsToIssue(issueId: Long, billIds: List<Long>)
}