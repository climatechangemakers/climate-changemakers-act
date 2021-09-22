package org.climatechangemakers.act.feature.issue.model

import kotlinx.serialization.Serializable

@Serializable data class Issue(
  val id: Long,
  val title: String,
  val talkingPoints: List<TalkingPoint>,
)

@Serializable data class TalkingPoint(
  val title: String,
  val content: String,
)