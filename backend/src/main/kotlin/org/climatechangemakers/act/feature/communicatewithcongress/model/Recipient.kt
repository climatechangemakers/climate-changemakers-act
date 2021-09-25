package org.climatechangemakers.act.feature.communicatewithcongress.model

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import org.climatechangemakers.act.common.serializers.YesNoBooleanSerializer

@Serializable class Recipient(
  @XmlElement(true) @XmlSerialName("MemberOffice", "", "") val officeCode: String,

  @XmlElement(true)
  @XmlSerialName("IsResponseRequested", "", "")
  @Serializable(with = YesNoBooleanSerializer::class)
  val responseRequested: Boolean = false,

  @XmlElement(true)
  @XmlSerialName("NewsletterOptIn", "", "")
  @Serializable(with = YesNoBooleanSerializer::class)
  val optInToNewsletter: Boolean = false,
)