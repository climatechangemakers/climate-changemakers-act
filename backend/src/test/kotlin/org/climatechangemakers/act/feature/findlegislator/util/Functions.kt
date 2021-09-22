package org.climatechangemakers.act.feature.findlegislator.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking

fun suspendTest(block: suspend CoroutineScope.() -> Unit) {
  runBlocking { block() }
}