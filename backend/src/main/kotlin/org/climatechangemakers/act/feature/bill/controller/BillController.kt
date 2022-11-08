package org.climatechangemakers.act.feature.bill.controller

import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import org.climatechangemakers.act.common.extension.respondNothing
import org.climatechangemakers.act.feature.bill.manager.BillManager
import org.climatechangemakers.act.feature.bill.model.Bill
import javax.inject.Inject

class BillController @Inject constructor(
  private val billManager: BillManager,
) {

  suspend fun postBill(call: ApplicationCall) {
    val bill = call.receive<Bill>()
    billManager.persistBill(bill)
    call.respondNothing()
  }
}