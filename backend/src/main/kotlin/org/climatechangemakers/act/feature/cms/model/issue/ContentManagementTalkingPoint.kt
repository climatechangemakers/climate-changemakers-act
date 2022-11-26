package org.climatechangemakers.act.feature.cms.model.issue

import kotlinx.serialization.Serializable

@Serializable data class ContentManagementTalkingPoint(
  val id: Long?,
  val title: String,
  val content: String,
  val relativeOrderPosition: Int,
)