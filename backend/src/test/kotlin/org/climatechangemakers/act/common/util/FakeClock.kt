package org.climatechangemakers.act.common.util

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class FakeClock(private val now: Instant) : Clock {

  override fun now(): Instant = now
}
