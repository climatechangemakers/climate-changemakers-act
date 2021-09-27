package org.climatechangemakers.act.feature.communicatewithcongress.model

import nl.adaptivity.xmlutil.serialization.XML
import kotlinx.serialization.encodeToString
import kotlin.test.Test
import kotlin.test.assertEquals

class RecipientSerializationTest {

  private val xml = XML {
    indentString = " "
    indent = 2
  }

  @Test fun `Recipient serializes correctly`() {
    val recipient = Recipient(
      officeCode = "STN02",
      responseRequested = true,
      optInToNewsletter = false,
    )

    assertEquals(
      """
        |<Recipient>
        |  <MemberOffice>STN02</MemberOffice>
        |  <IsResponseRequested>Y</IsResponseRequested>
        |  <NewsletterOptIn>N</NewsletterOptIn>
        |</Recipient>
      """.trimMargin(),
      xml.encodeToString(recipient)
    )
  }
}