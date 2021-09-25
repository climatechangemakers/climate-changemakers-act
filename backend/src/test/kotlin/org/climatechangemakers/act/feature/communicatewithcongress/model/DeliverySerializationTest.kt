package org.climatechangemakers.act.feature.communicatewithcongress.model

import kotlinx.serialization.encodeToString
import nl.adaptivity.xmlutil.serialization.XML
import org.junit.Test
import java.util.Calendar
import java.util.UUID
import kotlin.test.assertEquals

class DeliverySerializationTest {

  private val xml = XML {
    indentString = " "
    indent = 2
  }

  @Test fun `Delivery serializes correctly`() {
    val delivery = Delivery(
      deliveryId = UUID.fromString("19739ece-434e-488a-9857-a338a918a798"),
      deliveryDate = Calendar.getInstance().apply {
        set(Calendar.YEAR, 2020)
        set(Calendar.MONTH, Calendar.JANUARY)
        set(Calendar.DAY_OF_MONTH, 1)
      }.time,
      campaignId = "xyz",
    )
    assertEquals(
      """
        |<Delivery>
        |  <CampaignId>xyz</CampaignId>
        |  <DeliveryId>19739ece434e488a9857a338a918a798</DeliveryId>
        |  <DeliveryDate>20200101</DeliveryDate>
        |  <DeliveryAgent>Climate Changemakers</DeliveryAgent>
        |  <DeliveryAgentAckEmailAddress>info@climatechangemakers.org</DeliveryAgentAckEmailAddress>
        |  <DeliveryAgentContact>
        |    <DeliveryAgentContactName>Eliza Nemser</DeliveryAgentContactName>
        |    <DeliveryAgentContactEmail>eliza@climatechangemakers.org</DeliveryAgentContactEmail>
        |    <DeliveryAgentContactPhone>555-555-5555</DeliveryAgentContactPhone>
        |  </DeliveryAgentContact>
        |  <Organization>Climate Changemakers</Organization>
        |  <OrganizationAbout>Climate Changemakers is a non-partisan climate crisis mitigation advocacy group dedicated to ${'\n'}enacting climate policy. </OrganizationAbout>
        |</Delivery>
      """.trimMargin(),
      xml.encodeToString(delivery),
    )
  }
}