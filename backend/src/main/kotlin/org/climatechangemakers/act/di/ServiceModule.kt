package org.climatechangemakers.act.di

import org.climatechangemakers.act.feature.findlegislator.service.GeocodioService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json
import nl.adaptivity.xmlutil.serialization.XML
import okhttp3.MediaType
import okhttp3.OkHttpClient
import org.climatechangemakers.act.feature.communicatewithcongress.service.HouseCommunicateWithCongressService
import org.climatechangemakers.act.feature.communicatewithcongress.service.SenateCommunicateWithCongressService
import org.slf4j.Logger
import retrofit2.Retrofit

@Module object ServiceModule {

  private fun createOkHttpClient(
    apiKeyName: String,
    apiKey: String,
    logger: Logger,
  ): OkHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
    val originalRequest = chain.request()
    val originalUrl = originalRequest.url()
    val newUrl = originalUrl.newBuilder().addQueryParameter(apiKeyName, apiKey).build()

    logger.info("${originalRequest.method()} ${originalUrl.redact()}")

    chain.proceed(originalRequest.newBuilder().url(newUrl).build())
  }.build()

  @Provides @Geocodio fun providesGeocodioClient(logger: Logger): OkHttpClient = createOkHttpClient(
    apiKeyName = "api_key",
    apiKey = getEnvironmentVariable(EnvironmentVariable.GeocodioApiKey),
    logger = logger,
  )

  @Provides fun providesGeocodioService(@Geocodio client: OkHttpClient): GeocodioService = Retrofit.Builder()
    .baseUrl("https://api.geocod.io/v1.6/")
    .addConverterFactory(
      Json { ignoreUnknownKeys = true }.asConverterFactory(MediaType.get("application/json"))
    )
    .client(client)
    .build()
    .create(GeocodioService::class.java)

  @Provides @Senate fun providesSenateCWCClient(logger: Logger): OkHttpClient = createOkHttpClient(
    apiKeyName = "apikey",
    apiKey = getEnvironmentVariable(EnvironmentVariable.SCWCApiKey),
    logger = logger,
  )

  @Provides fun providesSenateCWCService(
    @Senate client: OkHttpClient,
    xml: XML,
  ): SenateCommunicateWithCongressService = Retrofit.Builder()
    .baseUrl(getEnvironmentVariable(EnvironmentVariable.SCWCUrl))
    .addConverterFactory(xml.asConverterFactory(MediaType.get("application/xml")))
    .client(client)
    .build()
    .create(SenateCommunicateWithCongressService::class.java)

  @Provides @House fun providesHouseCWCClient(logger: Logger): OkHttpClient = createOkHttpClient(
    apiKeyName = "apikey",
    apiKey = getEnvironmentVariable(EnvironmentVariable.HCWCApiKey),
    logger = logger,
  )

  @Provides fun providesHouseCWCService(
    @House client: OkHttpClient,
    xml: XML,
  ): HouseCommunicateWithCongressService = Retrofit.Builder()
    .baseUrl(getEnvironmentVariable(EnvironmentVariable.HCWCUrl))
    .addConverterFactory(xml.asConverterFactory(MediaType.get("application/xml")))
    .client(client)
    .build()
    .create(HouseCommunicateWithCongressService::class.java)
}