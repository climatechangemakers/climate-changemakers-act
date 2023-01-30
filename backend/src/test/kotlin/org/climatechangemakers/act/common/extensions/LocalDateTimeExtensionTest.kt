package org.climatechangemakers.act.common.extensions

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.climatechangemakers.act.common.extension.congressionalSession
import kotlin.test.Test
import kotlin.test.assertEquals

class LocalDateTimeExtensionTest {

  @Test fun `Jan 1 2023 is 117th Congress`() {
    val dt = LocalDateTime(
      year = 2023,
      monthNumber = 1,
      dayOfMonth = 1,
      hour = 0,
      minute = 0,
    ).toInstant(TimeZone.of("America/New_York"))

    assertEquals(expected = 117, actual = dt.congressionalSession)
  }

  @Test fun `Jan 3 2023 11 59AM 117th Congress`() {
    val dt = LocalDateTime(
      year = 2023,
      monthNumber = 1,
      dayOfMonth = 3,
      hour = 11,
      minute = 59,
    ).toInstant(TimeZone.of("America/New_York"))

    assertEquals(expected = 117, actual = dt.congressionalSession)
  }

  @Test fun `Jan 3 2023 12 00 118th Congress`() {
    val dt = LocalDateTime(
      year = 2023,
      monthNumber = 1,
      dayOfMonth = 3,
      hour = 12,
      minute = 0,
    ).toInstant(TimeZone.of("America/New_York"))

    assertEquals(expected = 118, actual = dt.congressionalSession)
  }

  @Test fun `Jan 4 2024 118th Congress`() {
    val dt = LocalDateTime(
      year = 2024,
      monthNumber = 1,
      dayOfMonth = 4,
      hour = 0,
      minute = 0,
    ).toInstant(TimeZone.of("America/New_York"))

    assertEquals(expected = 118, actual = dt.congressionalSession)
  }

  @Test fun `Jan 4 2029 121st Congress`() {
    val dt = LocalDateTime(
      year = 2029,
      monthNumber = 1,
      dayOfMonth = 4,
      hour = 0,
      minute = 0,
    ).toInstant(TimeZone.of("America/New_York"))

    assertEquals(expected = 121, actual = dt.congressionalSession)
  }
}