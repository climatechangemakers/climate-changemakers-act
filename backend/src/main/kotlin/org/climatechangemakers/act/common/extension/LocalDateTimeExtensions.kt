package org.climatechangemakers.act.common.extension

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

private const val FIRST_CONGRESS_YEAR = 1789

val Instant.congressionalSession: Short get() {
  val eastern = toLocalDateTime(TimeZone.of("America/New_York"))
  val sessionsElapsed = (eastern.year - FIRST_CONGRESS_YEAR) / 2

  // Congress is sworn in on January 3rd every year at noon eastern.
  val thirdDayOfYear = LocalDateTime(
    year = eastern.year,
    monthNumber = 1,
    dayOfMonth = 3,
    hour = 12,
    minute = 0,
  )
  return when  {
    eastern < thirdDayOfYear -> sessionsElapsed
    else -> sessionsElapsed + 1
  }.toShort()
}