package org.climatechangemakers.act.feature.communicatewithcongress.model

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlChildrenName
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import org.climatechangemakers.act.feature.bill.model.Bill
import org.climatechangemakers.act.feature.communicatewithcongress.serialization.CommunicatingWithCongressBillSerializer

@Serializable data class Message(
  @XmlElement(true) @XmlSerialName("Subject", "", "") val subject: String,

  @XmlElement(true)
  @XmlSerialName("LibraryOfCongressTopics", "", "")
  @XmlChildrenName("LibraryOfCongressTopic", "", "")
  val topics: List<Topic>,

  @XmlElement(true)
  @XmlSerialName("Bills", "", "")
  @XmlChildrenName("Bill", "", "")
  val bills: List<@Serializable(with = CommunicatingWithCongressBillSerializer::class) Bill>,

  @XmlElement(true) @XmlSerialName("ConstituentMessage", "", "") val body: String,
)
