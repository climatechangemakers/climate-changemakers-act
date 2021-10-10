package org.climatechangemakers.act.feature.communicatewithcongress.model

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
@XmlSerialName("CWC", namespace = "", prefix = "")
data class CommunicateWithCogressRequest(
  @XmlElement(true) @XmlSerialName("CWCVersion", "", "") val version: Double = 2.0,
  @XmlElement(true) val delivery: Delivery,
  @XmlElement(true) val recipient: Recipient,
  @XmlElement(true) val constituent: Constituent,
  @XmlElement(true) val message: Message,
)