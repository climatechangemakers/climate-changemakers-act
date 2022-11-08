package org.climatechangemakers.act.feature.bill.model

import kotlinx.serialization.Serializable


@Serializable class Bill(
  val congressionalSession: Short,
  val type: BillType,
  val number: Short,
  val name: String,
)