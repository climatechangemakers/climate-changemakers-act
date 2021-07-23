package com.climatechangemakers.act.feature.representativefinder.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking

fun suspendTest(block: suspend CoroutineScope.() -> Unit) {
  runBlocking { block() }
}