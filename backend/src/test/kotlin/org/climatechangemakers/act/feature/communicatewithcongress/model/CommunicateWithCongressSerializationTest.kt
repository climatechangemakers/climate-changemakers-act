package org.climatechangemakers.act.feature.communicatewithcongress.model

import kotlinx.serialization.encodeToString
import nl.adaptivity.xmlutil.XmlDeclMode
import nl.adaptivity.xmlutil.core.XmlVersion
import nl.adaptivity.xmlutil.serialization.XML
import org.climatechangemakers.act.common.model.RepresentedArea
import org.climatechangemakers.act.di.SerializationModule
import org.climatechangemakers.act.feature.bill.model.Bill
import org.climatechangemakers.act.feature.bill.model.BillType
import org.junit.Test
import java.util.Calendar
import java.util.Calendar.JANUARY
import java.util.UUID
import kotlin.test.assertEquals

class CommunicateWithCongressSerializationTest {

  private val xml = SerializationModule.providesXml()

  @Test fun `sample object structure serializes correctly`() {
    val request = CommunicateWithCogressRequest(
      delivery = Delivery(
        deliveryId = UUID.fromString("19739ece-434e-488a-9857-a338a918a798"),
        deliveryDate = Calendar.getInstance().apply {
          set(Calendar.YEAR, 2020)
          set(Calendar.MONTH, JANUARY)
          set(Calendar.DAY_OF_MONTH, 1)
        }.time,
        campaignId = "This is a campaign ID",
      ),
      recipient = Recipient(officeCode = "SWY01"),
      constituent = Constituent(
        prefix = Prefix.Mr,
        firstName = "Kevin",
        middleName = null,
        lastName = "McDoodle",
        suffix = null,
        title = null,
        address = "123 Main Street",
        city = "My City",
        state = RepresentedArea.Virginia,
        postalCode = "12345",
        email = "myemail@email.com",
      ),
      message = Message(
        subject = "This is a subject",
        topics = listOf(Topic.Energy, Topic.EnvironmentalProtection),
        bills = listOf(
          Bill(117, BillType.HouseBill, 2519, "some name")
        ),
        body = "foo",
      ),
    )

    assertEquals(
      """
        |<?xml version="1.0" encoding="UTF-8"?>
        |<CWC>
        |  <CWCVersion>2.0</CWCVersion>
        |  <Delivery>
        |    <CampaignId>This is a campaign ID</CampaignId>
        |    <DeliveryId>19739ece434e488a9857a338a918a798</DeliveryId>
        |    <DeliveryDate>20200101</DeliveryDate>
        |    <DeliveryAgent>Climate Changemakers</DeliveryAgent>
        |    <DeliveryAgentAckEmailAddress>info@climatechangemakers.org</DeliveryAgentAckEmailAddress>
        |    <DeliveryAgentContact>
        |      <DeliveryAgentContactName>Eliza Nemser</DeliveryAgentContactName>
        |      <DeliveryAgentContactEmail>info@climatechangemakers.org</DeliveryAgentContactEmail>
        |      <DeliveryAgentContactPhone>555-555-5555</DeliveryAgentContactPhone>
        |    </DeliveryAgentContact>
        |    <Organization>Climate Changemakers</Organization>
        |    <OrganizationAbout>Climate Changemakers is a non-partisan climate crisis mitigation advocacy group dedicated to ${'\n'}enacting climate policy. </OrganizationAbout>
        |  </Delivery>
        |  <Recipient>
        |    <MemberOffice>SWY01</MemberOffice>
        |    <IsResponseRequested>N</IsResponseRequested>
        |    <NewsletterOptIn>N</NewsletterOptIn>
        |  </Recipient>
        |  <Constituent>
        |    <Prefix>Mr.</Prefix>
        |    <FirstName>Kevin</FirstName>
        |    <LastName>McDoodle</LastName>
        |    <Address1>123 Main Street</Address1>
        |    <City>My City</City>
        |    <StateAbbreviation>VA</StateAbbreviation>
        |    <Zip>12345</Zip>
        |    <Email>myemail@email.com</Email>
        |  </Constituent>
        |  <Message>
        |    <Subject>This is a subject</Subject>
        |    <LibraryOfCongressTopics>
        |      <LibraryOfCongressTopic>Energy</LibraryOfCongressTopic>
        |      <LibraryOfCongressTopic>Environmental Protection</LibraryOfCongressTopic>
        |    </LibraryOfCongressTopics>
        |    <Bills>
        |      <Bill>
        |        <BillCongress>117</BillCongress>
        |        <BillTypeAbbreviation>H.R.</BillTypeAbbreviation>
        |        <BillNumber>2519</BillNumber>
        |      </Bill>
        |    </Bills>
        |    <ConstituentMessage>foo</ConstituentMessage>
        |  </Message>
        |</CWC>
      """.trimMargin(),
      xml.encodeToString(request),
    )
  }
}