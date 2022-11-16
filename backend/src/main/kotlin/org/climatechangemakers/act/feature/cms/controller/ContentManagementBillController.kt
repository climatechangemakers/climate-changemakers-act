package org.climatechangemakers.act.feature.cms.controller

import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import org.climatechangemakers.act.common.extension.respondNothing
import org.climatechangemakers.act.feature.bill.model.Bill
import org.climatechangemakers.act.feature.cms.manager.bill.ContentManagementBillManager
import javax.inject.Inject

class ContentManagementBillController @Inject constructor(
  private val billManager: ContentManagementBillManager,
) {

  suspend fun postBill(call: ApplicationCall) {
    val bill = call.receive<Bill>()
    billManager.persistBill(bill)
    call.respondNothing()
  }
}