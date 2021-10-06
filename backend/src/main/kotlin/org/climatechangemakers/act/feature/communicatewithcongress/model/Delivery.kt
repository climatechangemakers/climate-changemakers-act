package org.climatechangemakers.act.feature.communicatewithcongress.model

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import org.climatechangemakers.act.common.serializers.DateSerializer
import org.climatechangemakers.act.common.serializers.UUIDSerializer
import java.util.Date
import java.util.UUID

@Serializable class Delivery(

  @XmlElement(true)
  @XmlSerialName("CampaignId", "", "")
  val campaignId: String,

  @XmlElement(true)
  @XmlSerialName("DeliveryId", "", "")
  @Serializable(with = UUIDSerializer::class)
  val deliveryId: UUID = UUID.randomUUID(),

  @XmlElement(true)
  @XmlSerialName("DeliveryDate", "", "")
  @Serializable(with = DateSerializer::class)
  val deliveryDate: Date = Date(),
) {

  // Below are static values which don't need to be parametarized.

  @XmlElement(true)
  @XmlSerialName("DeliveryAgent", "", "")
  val deliveryAgent = "Climate Changemakers"

  @XmlElement(true)
  @XmlSerialName("DeliveryAgentAckEmailAddress", "", "")
  val deliveryAgentAckEmailAddress = "info@climatechangemakers.org"

  @XmlElement(true)
  val contact = DeliveryAgentContact(
    "Eliza Nemser",
    "info@climatechangemakers.org",
    "555-555-5555", // TODO(kcianfarini) get contact information
  )

  @XmlElement(true)
  @XmlSerialName("Organization", "", "")
  val organization = "Climate Changemakers"

  @XmlElement(true)
  @XmlSerialName("OrganizationAbout", "", "")
  val organizationAbout = """
    Climate Changemakers is a non-partisan climate crisis mitigation advocacy group dedicated to 
    enacting climate policy. 
  """.trimIndent()
}
