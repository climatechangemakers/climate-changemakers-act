package org.climatechangemakers.act.feature.communicatewithcongress.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure
import org.climatechangemakers.act.feature.bill.model.Bill
import org.climatechangemakers.act.feature.bill.model.BillType
import kotlinx.serialization.descriptors.element
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

object CommunicatingWithCongressBillSerializer : KSerializer<Bill> {

  override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Bill") {
    element<Short>(
      elementName = "congressionalSession",
      isOptional = false,
      annotations = listOf(
        XmlElement(true),
        XmlSerialName("BillCongress"),
      )
    )
    element<BillType>(
      elementName = "billType",
      isOptional = false,
      annotations = listOf(
        XmlElement(true),
        XmlSerialName("BillTypeAbbreviation"),
      )
    )
    element<Short>(
      elementName = "billNumber",
      isOptional = false,
      annotations = listOf(
        XmlElement(true),
        XmlSerialName("BillNumber"),
      )
    )
  }

  override fun deserialize(decoder: Decoder): Bill {
    TODO("Not yet implemented")
  }

  override fun serialize(
    encoder: Encoder,
    value: Bill
  ) = encoder.encodeStructure(descriptor) {
    encodeShortElement(index = 0, value = value.congressionalSession, descriptor = descriptor)
    encodeSerializableElement(
      index = 1,
      value = value.billType,
      descriptor = descriptor,
      serializer = BillType.serializer(),
    )
    encodeShortElement(index = 2, value = value.billNumber, descriptor = descriptor)
  }
}