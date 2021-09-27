package org.climatechangemakers.act.feature.communicatewithcongress.model

import kotlinx.serialization.encodeToString
import nl.adaptivity.xmlutil.serialization.XML
import org.junit.Test
import kotlin.test.assertEquals

class BillSerializationTest {

  private val xml = XML {
    indentString = " "
    indent = 2
  }

  @Test fun `Bill serializes correctly`() {
    val bill = Bill(117, BillType.HouseResolution, 1234)
    assertEquals(
      """
        |<Bill>
        |  <BillCongress>117</BillCongress>
        |  <BillTypeAbbreviation>H.Res.</BillTypeAbbreviation>
        |  <BillNumber>1234</BillNumber>
        |</Bill>
      """.trimMargin(),
      xml.encodeToString(bill)
    )
  }
}