package org.climatechangemakers.act.common.util

inline fun exists(existenceCondition: Boolean, lazyMessage: () -> String) {
  if (!existenceCondition) throw NoSuchElementException(lazyMessage())
}