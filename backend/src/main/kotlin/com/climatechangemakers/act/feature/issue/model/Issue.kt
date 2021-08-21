package com.climatechangemakers.act.feature.issue.model

import kotlinx.serialization.Serializable

@Serializable class Issue(
  val title: String,
  val talkingPoints: List<TalkingPoint>,
)

@Serializable class TalkingPoint(
  val title: String,
  val content: String,
)