package org.climatechangemakers.act.feature.bill.model

import kotlinx.serialization.Serializable


@Serializable class Bill(
  val congressionalSession: Short,
  val billType: BillType,
  val billNumber: Short,
  val billName: String,
)