package org.climatechangemakers.act.feature.cms.controller

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import org.climatechangemakers.act.common.extension.respondNothing
import org.climatechangemakers.act.feature.bill.model.Bill
import org.climatechangemakers.act.feature.cms.manager.bill.ContentManagementBillManager
import org.climatechangemakers.act.feature.cms.model.bill.CreateBill
import javax.inject.Inject

class ContentManagementBillController @Inject constructor(
  private val billManager: ContentManagementBillManager,
) {

  suspend fun postBill(call: ApplicationCall) {
    val bill = call.receive<CreateBill>()
    call.respond(
      status = HttpStatusCode.Created,
      message = billManager.persistBill(bill)
    )
  }

  suspend fun updateBill(call: ApplicationCall) {
    val bill = call.receive<Bill>()
    val billId = checkNotNull(call.parameters["id"]?.toLong())

    if (bill.id == billId) {
      call.respond(billManager.updateBill(bill))
    } else {
      call.respond(
        status = HttpStatusCode.BadRequest,
        message = "Attempting to update bill ${bill.id} at path $billId",
      )
    }
  }

  suspend fun getBills(call: ApplicationCall) {
    call.respond(billManager.getBills())
  }

  suspend fun deleteBill(call: ApplicationCall) {
    val billId = checkNotNull(call.parameters["id"]?.toLong())
    billManager.deleteBill(billId)
    call.respondNothing()
  }
}