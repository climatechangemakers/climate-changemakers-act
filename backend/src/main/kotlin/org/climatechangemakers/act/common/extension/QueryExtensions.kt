package org.climatechangemakers.act.common.extension

import app.cash.sqldelight.ExecutableQuery

fun <RowType : Any> ExecutableQuery<RowType>.executeAsOneOrNotFound(
  notFoundMessage: String = "No element found for $this.",
): RowType {
  return executeAsOneOrNull() ?: throw NoSuchElementException(notFoundMessage)
}