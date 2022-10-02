package org.climatechangemakers.act.common.util

import kotlinx.coroutines.delay
import retrofit2.HttpException
import retrofit2.Response

inline fun exists(existenceCondition: Boolean, lazyMessage: () -> String) {
  if (!existenceCondition) throw NoSuchElementException(lazyMessage())
}

fun <T> List<T>.joinToPhrase(
  separator: String = ", ",
  twoWordSeparator: String = " and ",
  endSeparator: String = ", and ",
  transform: (T) -> CharSequence = Any?::toString
): String = when (size) {
  in 0..2 -> joinToString(twoWordSeparator, transform = transform)
  else -> {
    val slice = subList(0, lastIndex)
    buildString {
      append(slice.joinToString(separator, transform = transform))
      append(endSeparator)
      append(transform(this@joinToPhrase.last()))
    }
  }
}