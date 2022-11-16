package org.climatechangemakers.act.feature.cms.manager.bill

import org.climatechangemakers.act.feature.bill.model.Bill

interface ContentManagementBillManager {

  suspend fun persistBill(bill: Bill)
}