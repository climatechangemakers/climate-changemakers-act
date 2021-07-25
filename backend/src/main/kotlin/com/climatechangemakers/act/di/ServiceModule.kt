package com.climatechangemakers.act.di

import com.climatechangemakers.act.feature.findlegislator.service.GoogleCivicInformationService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

@Module object ServiceModule {

  @Provides fun providesOkHttpClient(): OkHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
    val originalRequest = chain.request()
    val originalUrl = originalRequest.url()
    val newUrl = originalUrl.newBuilder()
      .addQueryParameter("key", requireNotNull(System.getenv("GOOGLE_CIVIC_API_KEY")))
      .build()

    chain.proceed(originalRequest.newBuilder().url(newUrl).build())
  }.build()

  @Provides fun providesGoogleCivicService(client: OkHttpClient): GoogleCivicInformationService = Retrofit.Builder()
    .baseUrl("https://civicinfo.googleapis.com/")
    .addConverterFactory(
      Json { ignoreUnknownKeys = true }.asConverterFactory(MediaType.get("application/json"))
    )
    .client(client)
    .build()
    .create(GoogleCivicInformationService::class.java)
}