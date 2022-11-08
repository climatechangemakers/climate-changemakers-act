package org.climatechangemakers.act.feature.communicatewithcongress.model

import nl.adaptivity.xmlutil.serialization.XML
import org.climatechangemakers.act.feature.bill.model.Bill
import org.climatechangemakers.act.feature.bill.model.BillType
import org.climatechangemakers.act.feature.communicatewithcongress.serialization.CommunicatingWithCongressBillSerializer
import org.junit.Test
import kotlin.test.assertEquals

class BillSerializationTest {

  private val xml = XML {
    indentString = " "
    indent = 2
  }

  @Test fun `bill serializes correctly`() {
    val bill = Bill(
      congressionalSession = 117,
      type = BillType.HouseResolution,
      number = 1234,
      name = "foo bar",
    )
    assertEquals(
      """
        |<Bill>
        |  <BillCongress>117</BillCongress>
        |  <BillTypeAbbreviation>H.Res.</BillTypeAbbreviation>
        |  <BillNumber>1234</BillNumber>
        |</Bill>
      """.trimMargin(),
      xml.encodeToString(value = bill, serializer = CommunicatingWithCongressBillSerializer)
    )
  }
}