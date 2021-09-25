package org.climatechangemakers.act.feature.communicatewithcongress.model

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable class DeliveryAgentContact(
  @XmlElement(true) @XmlSerialName("DeliveryAgentContactName", "", "") val contactName: String,
  @XmlElement(true) @XmlSerialName("DeliveryAgentContactEmail", "", "") val contactEmail: String,
  @XmlElement(true) @XmlSerialName("DeliveryAgentContactPhone", "", "") val contactPhoneNumber: String,
)
