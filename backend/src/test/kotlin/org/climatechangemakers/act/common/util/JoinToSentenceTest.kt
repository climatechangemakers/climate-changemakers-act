package org.climatechangemakers.act.common.util

import kotlin.test.Test
import kotlin.test.assertEquals

class JoinToPhraseTest {

  @Test fun `joining no elements leads to empty string`() {
    assertEquals("", emptyList<Any>().joinToPhrase())
  }

  @Test fun `joining one element has no separation characters`() {
    assertEquals("1", listOf(1).joinToPhrase())
  }

  @Test fun `joining two elements uses ending separator`() {
    assertEquals("1 and 2", listOf(1, 2).joinToPhrase())
  }

  @Test fun `joining three elements uses both separators`() {
    assertEquals("1, 2, and 3", listOf(1, 2, 3).joinToPhrase())
  }

  @Test fun `joining many elements yields correct result`() {
    assertEquals(
      "1, 2, 3, 4, 5, and 6",
      listOf(1, 2, 3, 4, 5, 6).joinToPhrase(),
    )
  }
}