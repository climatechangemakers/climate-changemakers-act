package org.climatechangemakers.act.feature.bill.model

import kotlinx.serialization.Serializable
import org.climatechangemakers.act.common.serializers.StringEnum
import org.climatechangemakers.act.common.serializers.StringEnumSerializer

@Serializable(with = BillTypeSerializer::class) enum class BillType(override val value: String) : StringEnum {
  HouseBill("H.R."),
  HouseConcurrentResolution("H.Con.Res."),
  HouseJointResolution("H.J.Res."),
  HouseResolution("H.Res."),
  SenateBill("S."),
  SenateConcurrentResolution("S.Con.Res."),
  SenateJointResolution("S.J.Res."),
  SenateResolution("S.Res."),
}

private object BillTypeSerializer : StringEnumSerializer<BillType>(BillType.values())
