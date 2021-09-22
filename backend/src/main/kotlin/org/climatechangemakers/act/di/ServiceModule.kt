package org.climatechangemakers.act.di

import org.climatechangemakers.act.feature.findlegislator.service.GeocodioService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

@Module object ServiceModule {

  @Provides @Geocodio fun providesOkHttpClient(): OkHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
    val originalRequest = chain.request()
    val originalUrl = originalRequest.url()
    val newUrl = originalUrl.newBuilder()
      .addQueryParameter("api_key", getEnvironmentVariable(EnvironmentVariable.GeocodioApiKey))
      .build()

    chain.proceed(originalRequest.newBuilder().url(newUrl).build())
  }.build()

  @Provides fun providesGeocodioService(@Geocodio client: OkHttpClient): GeocodioService = Retrofit.Builder()
    .baseUrl("https://api.geocod.io/v1.6/")
    .addConverterFactory(
      Json { ignoreUnknownKeys = true }.asConverterFactory(MediaType.get("application/json"))
    )
    .client(client)
    .build()
    .create(GeocodioService::class.java)
}