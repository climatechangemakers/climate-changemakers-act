package org.climatechangemakers.act.common.extension

import org.postgresql.util.PSQLException
import org.postgresql.util.PSQLState

val PSQLException.state: PSQLState get() {
  val map = PSQLState.values().associateBy { it.state }
  return map.getValue(this.sqlState)
}