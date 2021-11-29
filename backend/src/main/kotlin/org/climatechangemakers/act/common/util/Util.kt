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

/**
 * A simple exponential backoff handler that will rerun [block] up to [attempts] times in the event of an HTTP 429 response.
 * Between each invocation, delays will increase exponentially without jitter.
 *
 * A record of which attempt is currently being invoked is passed to [block] and is 0 indexed.
 *
 * In the event where all attempts are exhausted an exception will be thrown from the most recent attempt.
 */
suspend inline fun <T> withRetry(attempts: Int, block: (attempt: Int) -> Response<T>): T {
  require(attempts > 1)
  var response: Response<T>? = null

  for (attempt in 0 until attempts) {
    delay(attempt * attempt * 1000L)
    response = block(attempt)

    return when {
      response.isSuccessful -> response.body()!!
      response.code() == 429 -> continue
      else -> throw HttpException(response)
    }
  }

  throw HttpException(response!!)
}