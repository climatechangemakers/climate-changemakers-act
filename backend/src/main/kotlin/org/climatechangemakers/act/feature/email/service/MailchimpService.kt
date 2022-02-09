package org.climatechangemakers.act.feature.email.service

import org.climatechangemakers.act.feature.email.model.EnrollMemberRequest
import org.climatechangemakers.act.feature.email.model.SubscribeNewsletterRequest
import retrofit2.Response
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Body
import retrofit2.http.GET

interface MailchimpService {

  @PUT("lists/{audience_id}/members/{email_md5_hash_string}")
  suspend fun subscribeChangemaker(
    @Path("audience_id") audienceId: String,
    @Path("email_md5_hash_string") emailMd5Hash: String,
    @Body request: EnrollMemberRequest,
  )

  @PUT("lists/{audience_id}/members/{email_md5_hash_string}")
  suspend fun subscribeChangemaker(
    @Path("audience_id") audienceId: String,
    @Path("email_md5_hash_string") emailMd5Hash: String,
    @Body request: SubscribeNewsletterRequest,
  )

  @GET("lists/{audience_id}/members/{email_md5_hash_string}")
  suspend fun checkSubscription(
    @Path("audience_id") audienceId: String,
    @Path("email_md5_hash_string") emailMd5Hash: String,
  ): Response<Void>
}