package org.climatechangemakers.act.common.model

import kotlinx.serialization.Serializable
import org.climatechangemakers.act.common.serializers.StringEnum
import org.climatechangemakers.act.common.serializers.StringEnumSerializer

@Serializable(with = StateSerializer::class) enum class State(override val value: String) : StringEnum {
  Alaska("AK"),
  Alabama("AL"),
  AmericanSamoa("AS"),
  Arizona("AZ"),
  Arkansas("AR"),
  California("CA"),
  Colorado("CO"),
  Connecticut("CT"),
  DistrictOfColumbia("DC"),
  Delaware("DE"),
  Florida("FL"),
  Georgia("GA"),
  Guam("GU"),
  Hawaii("HI"),
  Idaho("ID"),
  Illinois("IL"),
  Indiana("IN"),
  Iowa("IA"),
  Kansas("KS"),
  Kentucky("KY"),
  Louisiana("LA"),
  Maine("ME"),
  Maryland("MD"),
  Massachusetts("MA"),
  Michigan("MI"),
  Minnesota("MN"),
  Mississippi("MS"),
  Missouri("MO"),
  Montana("MT"),
  Nebraska("NE"),
  Nevada("NV"),
  NewHampshire("NH"),
  NewJersey("NJ"),
  NewMexico("NM"),
  NewYork("NY"),
  NorthCarolina("NC"),
  NorthDakota("ND"),
  NorthernMarianaIslands("MP"),
  Ohio("OH"),
  Oklahoma("OK"),
  Oregon("OR"),
  Pennsylvania("PA"),
  PuertoRico("PR"),
  RhodeIsland("RI"),
  SouthCarolina("SC"),
  SouthDakota("SD"),
  Tennessee("TN"),
  Texas("TX"),
  Utah("UT"),
  VirginIslands("VI"),
  Vermont("VT"),
  Virginia("VA"),
  Washington("WA"),
  WestVirginia("WV"),
  Wisconsin("WI"),
  Wyoming("WY");

  override fun toString() = value
}

object StateSerializer : StringEnumSerializer<State>(State.values())