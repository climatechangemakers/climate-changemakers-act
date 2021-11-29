package org.climatechangemakers.act.common.util

import okhttp3.MediaType
import okhttp3.ResponseBody
import org.climatechangemakers.act.feature.findlegislator.util.suspendTest
import org.climatechangemakers.act.feature.membership.model.AirtableRecord
import org.climatechangemakers.act.feature.membership.model.AirtableResponse
import retrofit2.HttpException
import retrofit2.Response
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class WithRetryTest {

  @Test fun `withRetry throws exception for most recent failure`() = suspendTest {
    val throwable = assertFailsWith<HttpException> {
      withRetry(2) { attempt ->
        Response.error(
          429,
          ResponseBody.create(MediaType.get("application/json"), "too much $attempt"),
        )
      }
    }

    assertEquals(429, throwable.code())
    assertEquals("too much 1", throwable.response()?.errorBody()?.string())
  }

  @Test fun `withRetry throws exception for non 429 status code`() = suspendTest {
    val throwable = assertFailsWith<HttpException> {
      withRetry(2) {
        Response.error(
         400,
          ResponseBody.create(MediaType.get("application/json"), "blah"),
        )
      }
    }

    assertEquals(400, throwable.code())
  }

  @Test fun `withRetry returns first successful response`() = suspendTest {
    val response = withRetry(2) { attempt ->
      if (attempt == 0) {
        Response.error(429, ResponseBody.create(MediaType.get("application/json"), "please retry"))
      } else {
        Response.success(AirtableResponse(records = listOf(AirtableRecord(id = "foo"))))
      }
    }

    assertEquals("foo", response.records.first().id)
  }
}