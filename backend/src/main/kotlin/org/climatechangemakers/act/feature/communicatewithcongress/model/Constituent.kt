package org.climatechangemakers.act.feature.communicatewithcongress.model

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import org.climatechangemakers.act.common.model.RepresentedArea

@Serializable class Constituent(
  @XmlElement(true) @XmlSerialName("Prefix", "", "") val prefix: Prefix,
  @XmlElement(true) @XmlSerialName("FirstName", "", "") val firstName: String,
  @XmlElement(true) @XmlSerialName("MiddleName", "", "") val middleName: String? = null,
  @XmlElement(true) @XmlSerialName("LastName", "", "") val lastName: String,
  @XmlElement(true) @XmlSerialName("Suffix", "", "") val suffix: String?= null,
  @XmlElement(true) @XmlSerialName("Title", "", "") val title: String? = null,
  @XmlElement(true) @XmlSerialName("Address1", "", "") val address: String,
  @XmlElement(true) @XmlSerialName("City", "", "") val city: String,
  @XmlElement(true) @XmlSerialName("StateAbbreviation", "", "") val state: RepresentedArea,
  @XmlElement(true) @XmlSerialName("Zip", "", "") val postalCode: String,
  @XmlElement(true) @XmlSerialName("Email", "", "") val email: String,
)
