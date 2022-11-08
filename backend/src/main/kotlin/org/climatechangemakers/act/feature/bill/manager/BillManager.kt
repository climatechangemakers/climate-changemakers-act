package org.climatechangemakers.act.feature.bill.manager

import org.climatechangemakers.act.feature.bill.model.Bill

interface BillManager {

  suspend fun persistBill(bill: Bill)
}