package org.climatechangemakers.act.feature.email.service

import org.climatechangemakers.act.feature.email.model.SubscribeChangemakerRequest
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Body

interface MailchimpService {

  @PUT("lists/{audience_id}/members/{email_md5_hash_string}")
  suspend fun subscribeChangemaker(
    @Path("audience_id") audienceId: String,
    @Path("email_md5_hash_string") emailMd5Hash: String,
    @Body request: SubscribeChangemakerRequest,
  )
}