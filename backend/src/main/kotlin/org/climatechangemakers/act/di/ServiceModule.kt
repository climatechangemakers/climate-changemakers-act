package org.climatechangemakers.act.di

import org.climatechangemakers.act.feature.findlegislator.service.GeocodioService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import nl.adaptivity.xmlutil.serialization.XML
import okhttp3.*
import org.climatechangemakers.act.feature.communicatewithcongress.service.HouseCommunicateWithCongressService
import org.climatechangemakers.act.feature.communicatewithcongress.service.SenateCommunicateWithCongressService
import org.climatechangemakers.act.feature.findlegislator.service.GoogleCivicService
import org.climatechangemakers.act.feature.membership.service.AirtableService
import org.slf4j.Logger
import retrofit2.Converter
import retrofit2.Converter.Factory
import retrofit2.Retrofit

@Module object ServiceModule {

  private fun createUrlApiKeyOkHttpClient(
    apiKeyName: String,
    apiKey: String,
    logger: Logger,
  ): OkHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
    val originalRequest = chain.request()
    val originalUrl = originalRequest.url()
    val newUrl = originalUrl.newBuilder().addQueryParameter(apiKeyName, apiKey).build()

    logger.info("${originalRequest.method()} ${originalUrl.redactAndRetainPath()}")
    chain.proceed(originalRequest.newBuilder().url(newUrl).build())
  }.build()

  private fun createBearerTokenOkHttpClient(
    apiKey: String,
    logger: Logger,
  ): OkHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
    val newRequest = chain.request().newBuilder()
      .addHeader("Authorization", "Bearer $apiKey")
      .build()

    logger.info("${newRequest.method()} ${newRequest.url().redactAndRetainPath()}")
    chain.proceed(newRequest)
  }.build()

  private fun createBasicAuthOkHttpClient(
    user: String,
    apiKey: String,
    logger: Logger,
  ): OkHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
    val newRequest = chain.request().newBuilder()
      .addHeader("Authorization", Credentials.basic(user, apiKey))
      .build()

    logger.info("${newRequest.method()} ${newRequest.url().redactAndRetainPath()}")
    chain.proceed(newRequest)
  }.build()

  @Provides @GoogleCivic fun providesGoogleCivicClient(logger: Logger): OkHttpClient = createUrlApiKeyOkHttpClient(
    apiKeyName = "key",
    apiKey = getEnvironmentVariable(EnvironmentVariable.GoogleCivicApiKey),
    logger = logger,
  )

  @Provides fun providesGoogleCivicService(
    @GoogleCivic client: OkHttpClient,
    jsonConverterFactory: Factory,
  ): GoogleCivicService = Retrofit.Builder()
    .baseUrl("https://civicinfo.googleapis.com/civicinfo/v2/")
    .addConverterFactory(jsonConverterFactory)
    .client(client)
    .build()
    .create(GoogleCivicService::class.java)

  @Provides @Geocodio fun providesGeocodioClient(logger: Logger): OkHttpClient = createUrlApiKeyOkHttpClient(
    apiKeyName = "api_key",
    apiKey = getEnvironmentVariable(EnvironmentVariable.GeocodioApiKey),
    logger = logger,
  )

  @Provides fun providesGeocodioService(
    @Geocodio client: OkHttpClient,
    jsonConverterFactory: Converter.Factory,
  ): GeocodioService = Retrofit.Builder()
    .baseUrl("https://api.geocod.io/v1.6/")
    .addConverterFactory(jsonConverterFactory)
    .client(client)
    .build()
    .create(GeocodioService::class.java)

  @Provides @Senate fun providesSenateCWCClient(logger: Logger): OkHttpClient = createUrlApiKeyOkHttpClient(
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

  @Provides @House fun providesHouseCWCClient(logger: Logger): OkHttpClient = createUrlApiKeyOkHttpClient(
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

  @Provides @Airtable fun providesAirtableClient(logger: Logger): OkHttpClient = createBearerTokenOkHttpClient(
    logger = logger,
    apiKey = getEnvironmentVariable(EnvironmentVariable.AirtableApiKey),
  )

  @Provides fun providesAirtableService(
    @Airtable client: OkHttpClient,
    jsonConverterFactory: Converter.Factory,
  ): AirtableService = Retrofit.Builder()
    .baseUrl("https://api.airtable.com/v0/${getEnvironmentVariable(EnvironmentVariable.AirtableBaseId)}/CRM/")
    .addConverterFactory(jsonConverterFactory)
    .client(client)
    .build()
    .create(AirtableService::class.java)
}

private fun HttpUrl.redactAndRetainPath(): String = newBuilder()
  .username("")
  .password("")
  .fragment(null)
  .query(null)
  .build()
  .toString()