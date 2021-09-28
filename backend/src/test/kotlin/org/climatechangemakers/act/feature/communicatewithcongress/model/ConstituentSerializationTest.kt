package org.climatechangemakers.act.feature.communicatewithcongress.model

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.serializersModuleOf
import nl.adaptivity.xmlutil.serialization.XML
import org.climatechangemakers.act.common.model.RepresentedArea
import org.climatechangemakers.act.common.model.RepresentedAreaSerializer
import org.junit.Test
import kotlin.test.assertEquals

class ConstituentSerializationTest {

  private val xml = XML {
    indentString = " "
    indent = 2
  }

  @Test fun `Constituent serializes correctly`() {
    val constituent = Constituent(
      prefix = Prefix.Dr,
      firstName = "Kevin",
      middleName = "Bob",
      lastName = "McDoodle",
      suffix = "Jr.",
      title = "Climate Change Advocate",
      address = "123 Main Street",
      city = "City",
      state = RepresentedArea.WestVirginia,
      postalCode = "12345",
      email = "fake@fake.org",
    )

    assertEquals(
      """
        |<Constituent>
        |  <Prefix>Dr.</Prefix>
        |  <FirstName>Kevin</FirstName>
        |  <MiddleName>Bob</MiddleName>
        |  <LastName>McDoodle</LastName>
        |  <Suffix>Jr.</Suffix>
        |  <Title>Climate Change Advocate</Title>
        |  <Address1>123 Main Street</Address1>
        |  <City>City</City>
        |  <StateAbbreviation>WV</StateAbbreviation>
        |  <Zip>12345</Zip>
        |  <Email>fake@fake.org</Email>
        |</Constituent>
      """.trimMargin(),
      xml.encodeToString(constituent),
    )
  }
}