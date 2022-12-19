package org.climatechangemakers.act.feature.cms.model.issue

import kotlinx.serialization.Serializable

@Serializable data class ContentManagementIssue(
  val id: Long?,
  val title: String,
  val precomposedTweetTemplate: String,
  val imageUrl: String,
  val description: String,
  val isFocusIssue: Boolean,
  val talkingPoints: List<ContentManagementTalkingPoint>,
  val relatedBillIds: List<Long>,
)