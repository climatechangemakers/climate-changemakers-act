package org.climatechangemakers.act.feature.bill.model

import kotlinx.serialization.Serializable


@Serializable data class Bill(
  val id: Long,
  val congressionalSession: Short,
  val type: BillType,
  val number: Short,
  val name: String,
  val url: String,
)