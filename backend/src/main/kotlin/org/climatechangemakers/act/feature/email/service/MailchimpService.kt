package org.climatechangemakers.act.feature.email.service

import org.climatechangemakers.act.feature.email.model.SubscribeChangemakerRequest
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface MailchimpService {

  @POST("lists/{audience_id}/members/")
  suspend fun subscribeChangemaker(
    @Path("audience_id") audienceId: String,
    @Body request: SubscribeChangemakerRequest
  )
}