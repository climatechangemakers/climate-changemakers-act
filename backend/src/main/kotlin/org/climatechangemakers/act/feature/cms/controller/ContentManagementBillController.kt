package org.climatechangemakers.act.feature.cms.controller

import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import org.climatechangemakers.act.common.extension.respondNothing
import org.climatechangemakers.act.feature.cms.manager.bill.ContentManagementBillManager
import org.climatechangemakers.act.feature.cms.model.bill.CreateBill
import javax.inject.Inject

class ContentManagementBillController @Inject constructor(
  private val billManager: ContentManagementBillManager,
) {

  suspend fun postBill(call: ApplicationCall) {
    val bill = call.receive<CreateBill>()
    billManager.persistBill(bill)
    call.respondNothing()
  }

  suspend fun getBills(call: ApplicationCall) {
    call.respond(billManager.getBills())
  }
}