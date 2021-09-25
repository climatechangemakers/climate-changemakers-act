package org.climatechangemakers.act.feature.communicatewithcongress.model

import kotlinx.serialization.Serializable
import org.climatechangemakers.act.common.serializers.StringEnum
import org.climatechangemakers.act.common.serializers.StringEnumSerializer

@Serializable(with = TopicSerializer::class) enum class Topic(override val value: String) : StringEnum {
  AgricultureAndFood("Agriculture and Food"),
  Animals("Animals"),
  ArmedForcesAndNationalSecurity("Armed Forces and National Security"),
  ArtsCultureReligion("Arts, Culture, Religion"),
  CivilRightsMinorityIssues("Civil Rights and Liberties, Minority Issues"),
  Commerce("Commerce"),
  Congress("Congress"),
  CrimeAndLawEnforcement("Crime and Law Enforcement"),
  EconomicsAndPublicFinance("Economics and Public Finance"),
  Education("Education"),
  EmergencyManagement("Emergency Management"),
  Energy("Energy"),
  EnvironmentalProtection("Environmental Protection"),
  Families("Families"),
  FinanceAndFinancialSector("Finance and Financial Sector"),
  ForeignTradeAndInternationalFinance("Foreign Trade and International Finance"),
  GovernmentOperationsAndPolitics("Government Operations and Politics"),
  Health("Health"),
  HousingAndCommunityDevelopment("Housing and Community Development"),
  Immigration("Immigration"),
  InternationalAffairs("International Affairs"),
  LaborAndEmployment("Labor and Employment"),
  Law("Law"),
  NativeAmericans("Native Americans"),
  PublicLandsAndNaturalResources("Public Lands and Natural Resources"),
  ScienceTechnologyCommunications("Science, Technology, Communications"),
  SocialSciencesAndHistory("Social Sciences and History"),
  SocialWelfare("Social Welfare"),
  SportsAndRecreation("Sports and Recreation"),
  Taxation("Taxation"),
  TransportationAndPublicWorks("Transportation and Public Works"),
  WaterResourcesDevelopment("Water Resources Development"),
}

private object TopicSerializer : StringEnumSerializer<Topic>(Topic.values())
