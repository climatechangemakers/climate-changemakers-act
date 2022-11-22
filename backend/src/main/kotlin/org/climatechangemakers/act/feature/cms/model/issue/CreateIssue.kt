package org.climatechangemakers.act.feature.cms.model.issue

import kotlinx.serialization.Serializable

@Serializable class CreateIssue(
  val title: String,
  val precomposedTweetTemplate: String,
  val imageUrl: String,
  val description: String,
  val isFocusIssue: Boolean,
)