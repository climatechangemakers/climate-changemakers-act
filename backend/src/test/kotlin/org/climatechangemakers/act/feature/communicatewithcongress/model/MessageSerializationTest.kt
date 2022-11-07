package org.climatechangemakers.act.feature.communicatewithcongress.model

import kotlinx.serialization.encodeToString
import nl.adaptivity.xmlutil.serialization.XML
import org.climatechangemakers.act.feature.bill.model.Bill
import org.climatechangemakers.act.feature.bill.model.BillType
import org.junit.Test
import kotlin.test.assertEquals

class MessageSerializationTest {

  private val xml = XML {
    indentString = " "
    indent = 2
  }

  @Test fun `message serializes correctly`() {
    val message = Message(
      subject = "America needs to keep it in the ground",
      topics = Topic.values().toList(),
      body = "Fucking do something about climate change, congress.",
      bills = listOf(
        Bill(117, BillType.SenateBill, 1234, "some name")
      ),
    )

    assertEquals(
      """
        |<Message>
        |  <Subject>America needs to keep it in the ground</Subject>
        |  <LibraryOfCongressTopics>
        |    <LibraryOfCongressTopic>Agriculture and Food</LibraryOfCongressTopic>
        |    <LibraryOfCongressTopic>Animals</LibraryOfCongressTopic>
        |    <LibraryOfCongressTopic>Armed Forces and National Security</LibraryOfCongressTopic>
        |    <LibraryOfCongressTopic>Arts, Culture, Religion</LibraryOfCongressTopic>
        |    <LibraryOfCongressTopic>Civil Rights and Liberties, Minority Issues</LibraryOfCongressTopic>
        |    <LibraryOfCongressTopic>Commerce</LibraryOfCongressTopic>
        |    <LibraryOfCongressTopic>Congress</LibraryOfCongressTopic>
        |    <LibraryOfCongressTopic>Crime and Law Enforcement</LibraryOfCongressTopic>
        |    <LibraryOfCongressTopic>Economics and Public Finance</LibraryOfCongressTopic>
        |    <LibraryOfCongressTopic>Education</LibraryOfCongressTopic>
        |    <LibraryOfCongressTopic>Emergency Management</LibraryOfCongressTopic>
        |    <LibraryOfCongressTopic>Energy</LibraryOfCongressTopic>
        |    <LibraryOfCongressTopic>Environmental Protection</LibraryOfCongressTopic>
        |    <LibraryOfCongressTopic>Families</LibraryOfCongressTopic>
        |    <LibraryOfCongressTopic>Finance and Financial Sector</LibraryOfCongressTopic>
        |    <LibraryOfCongressTopic>Foreign Trade and International Finance</LibraryOfCongressTopic>
        |    <LibraryOfCongressTopic>Government Operations and Politics</LibraryOfCongressTopic>
        |    <LibraryOfCongressTopic>Health</LibraryOfCongressTopic>
        |    <LibraryOfCongressTopic>Housing and Community Development</LibraryOfCongressTopic>
        |    <LibraryOfCongressTopic>Immigration</LibraryOfCongressTopic>
        |    <LibraryOfCongressTopic>International Affairs</LibraryOfCongressTopic>
        |    <LibraryOfCongressTopic>Labor and Employment</LibraryOfCongressTopic>
        |    <LibraryOfCongressTopic>Law</LibraryOfCongressTopic>
        |    <LibraryOfCongressTopic>Native Americans</LibraryOfCongressTopic>
        |    <LibraryOfCongressTopic>Public Lands and Natural Resources</LibraryOfCongressTopic>
        |    <LibraryOfCongressTopic>Science, Technology, Communications</LibraryOfCongressTopic>
        |    <LibraryOfCongressTopic>Social Sciences and History</LibraryOfCongressTopic>
        |    <LibraryOfCongressTopic>Social Welfare</LibraryOfCongressTopic>
        |    <LibraryOfCongressTopic>Sports and Recreation</LibraryOfCongressTopic>
        |    <LibraryOfCongressTopic>Taxation</LibraryOfCongressTopic>
        |    <LibraryOfCongressTopic>Transportation and Public Works</LibraryOfCongressTopic>
        |    <LibraryOfCongressTopic>Water Resources Development</LibraryOfCongressTopic>
        |  </LibraryOfCongressTopics>
        |  <Bills>
        |    <Bill>
        |      <BillCongress>117</BillCongress>
        |      <BillTypeAbbreviation>S.</BillTypeAbbreviation>
        |      <BillNumber>1234</BillNumber>
        |    </Bill>
        |  </Bills>
        |  <ConstituentMessage>Fucking do something about climate change, congress.</ConstituentMessage>
        |</Message>
      """.trimMargin(),
      xml.encodeToString(message)
    )
  }
}