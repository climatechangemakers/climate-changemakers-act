package org.climatechangemakers.act.feature.findlegislator.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

fun suspendTest(block: suspend CoroutineScope.() -> Unit) {
  runBlocking { block() }
}