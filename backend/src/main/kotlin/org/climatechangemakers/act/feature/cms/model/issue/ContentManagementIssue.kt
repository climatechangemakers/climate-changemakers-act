package org.climatechangemakers.act.feature.cms.model.issue

import kotlinx.serialization.Serializable

sealed interface ContentManagementIssue {

  val title: String
  val precomposedTweetTemplate: String
  val imageUrl: String
  val description: String
  val isFocusIssue: Boolean
  val talkingPoints: List<ContentManagementTalkingPoint>
  val relatedBillIds: List<Long>

  @Serializable data class New(
    override val title: String,
    override val precomposedTweetTemplate: String,
    override val imageUrl: String,
    override val description: String,
    override val isFocusIssue: Boolean,
    override val talkingPoints: List<ContentManagementTalkingPoint>,
    override val relatedBillIds: List<Long>
  ) : ContentManagementIssue

  @Serializable data class Persisted(
    val id: Long,
    override val title: String,
    override val precomposedTweetTemplate: String,
    override val imageUrl: String,
    override val description: String,
    override val isFocusIssue: Boolean,
    override val talkingPoints: List<ContentManagementTalkingPoint>,
    override val relatedBillIds: List<Long>
  ) : ContentManagementIssue
}