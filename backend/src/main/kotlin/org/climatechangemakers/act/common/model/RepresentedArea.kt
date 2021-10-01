package org.climatechangemakers.act.common.model

import kotlinx.serialization.Serializable
import org.climatechangemakers.act.common.serializers.StringEnum
import org.climatechangemakers.act.common.serializers.StringEnumSerializer

/**
 * An area which has at least one representative present in Congress.
 */
@Serializable(with = RepresentedAreaSerializer::class)
enum class RepresentedArea(override val value: String, val fullName: String) : StringEnum {
  Alaska("AK", "Alaska"),
  Alabama("AL", "Alabama"),
  AmericanSamoa("AS", "American Samoa"),
  Arizona("AZ", "Arizona"),
  Arkansas("AR", "Arkansas"),
  California("CA", "California"),
  Colorado("CO", "Colorado"),
  Connecticut("CT", "Connecticut"),
  DistrictOfColumbia("DC", "District Of Columbia"),
  Delaware("DE", "Delaware"),
  Florida("FL", "Florida"),
  Georgia("GA", "Georgia"),
  Guam("GU", "Guam"),
  Hawaii("HI", "Hawaii"),
  Idaho("ID", "Idaho"),
  Illinois("IL", "Illinois"),
  Indiana("IN", "Indiana"),
  Iowa("IA", "Iowa"),
  Kansas("KS", "Kansas"),
  Kentucky("KY", "Kentucky"),
  Louisiana("LA", "Louisiana"),
  Maine("ME", "Maine"),
  Maryland("MD", "Maryland"),
  Massachusetts("MA", "Massachusetts"),
  Michigan("MI", "Michigan"),
  Minnesota("MN", "Minnesota"),
  Mississippi("MS", "Mississippi"),
  Missouri("MO", "Missouri"),
  Montana("MT", "Montana"),
  Nebraska("NE", "Nebraska"),
  Nevada("NV", "Nevada"),
  NewHampshire("NH", "New Hampshire"),
  NewJersey("NJ", "New Jersey"),
  NewMexico("NM", "New Mexico"),
  NewYork("NY", "New York"),
  NorthCarolina("NC", "North Carolina"),
  NorthDakota("ND", "North Dakota"),
  NorthernMarianaIslands("MP", "Northern Mariana Islands"),
  Ohio("OH", "Ohio"),
  Oklahoma("OK", "Oklahoma"),
  Oregon("OR", "Oregon"),
  Pennsylvania("PA", "Pennsylvania"),
  PuertoRico("PR", "Puerto Rico"),
  RhodeIsland("RI", "Rhode Island"),
  SouthCarolina("SC", "South Carolina"),
  SouthDakota("SD", "South Dakota"),
  Tennessee("TN", "Tennessee"),
  Texas("TX", "Texas"),
  Utah("UT", "Utah"),
  VirginIslands("VI", "Virgin Islands"),
  Vermont("VT", "Vermont"),
  Virginia("VA", "Virginia"),
  Washington("WA", "Washington"),
  WestVirginia("WV", "West Virginia"),
  Wisconsin("WI", "Wisconsin"),
  Wyoming("WY", "Wyoming");

  override fun toString() = value
}

object RepresentedAreaSerializer : StringEnumSerializer<RepresentedArea>(RepresentedArea.values())