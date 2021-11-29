package org.climatechangemakers.act.feature.membership.service

sealed interface AirtableFormula {

  override fun toString(): String

  class FilterByEmailFormula(val email: String) : AirtableFormula {
    override fun toString(): String = "{Email} = '$email'"
  }
}