package org.climatechangemakers.act.feature.cms.manager.bill

import org.climatechangemakers.act.feature.bill.model.Bill
import org.climatechangemakers.act.feature.cms.model.bill.CreateBill

interface ContentManagementBillManager {

  suspend fun persistBill(bill: CreateBill)

  suspend fun getBills(): List<Bill>
}