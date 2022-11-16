package org.climatechangemakers.act.feature.cms.model.bill

import kotlinx.serialization.Serializable
import org.climatechangemakers.act.feature.bill.model.BillType

@Serializable class CreateBill(
  val congressionalSession: Short,
  val type: BillType,
  val number: Short,
  val name: String,
  val url: String,
)