package org.climatechangemakers.act.feature.communicatewithcongress.model

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import org.climatechangemakers.act.common.serializers.StringEnum
import org.climatechangemakers.act.common.serializers.StringEnumSerializer

@Serializable class Bill(
  @XmlElement(true) @XmlSerialName("BillCongress", "", "") val congressNumber: Int,
  @XmlElement(true) @XmlSerialName("BillTypeAbbreviation", "", "") val billType: BillType,
  @XmlElement(true) @XmlSerialName("BillNumber", "", "") val billNumber: Int,
)

@Serializable(with = BillTypeSerializer::class) enum class BillType(override val value: String) : StringEnum {
  HouseAmendment("H.Amdt."),
  HouseConcurrentResolution("H.Con.Res."),
  HouseJointResolution("H.J.Res."),
  HouseBill("H.R."),
  HouseResolution("H.Res."),

  SenateAmendment("S.Amdt."),
  SenateConcurrentResolution("S.Con.Res."),
  SenateJointResolution("S.J.Res."),
  SenateBill("S."),
  SenateResolution("S.Res."),
}

private object BillTypeSerializer : StringEnumSerializer<BillType>(BillType.values())