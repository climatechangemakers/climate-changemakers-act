package org.climatechangemakers.act.di

import org.climatechangemakers.act.feature.findlegislator.service.GeocodioService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json
import nl.adaptivity.xmlutil.XmlDeclMode
import nl.adaptivity.xmlutil.serialization.XML
import okhttp3.MediaType
import okhttp3.OkHttpClient
import org.climatechangemakers.act.feature.communicatewithcongress.service.CommunicateWithCongressService
import retrofit2.Retrofit

@Module object ServiceModule {

  private fun createOkHttpClient(
    apiKeyName: String,
    apiKey: String,
  ): OkHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
    val originalRequest = chain.request()
    val originalUrl = originalRequest.url()
    val newUrl = originalUrl.newBuilder().addQueryParameter(apiKeyName, apiKey).build()

    chain.proceed(originalRequest.newBuilder().url(newUrl).build())
  }.build()

  @Provides @Geocodio fun providesGeocodioClient(): OkHttpClient = createOkHttpClient(
    apiKeyName = "api_key",
    apiKey = getEnvironmentVariable(EnvironmentVariable.GeocodioApiKey),
  )

  @Provides fun providesGeocodioService(@Geocodio client: OkHttpClient): GeocodioService = Retrofit.Builder()
    .baseUrl("https://api.geocod.io/v1.6/")
    .addConverterFactory(
      Json { ignoreUnknownKeys = true }.asConverterFactory(MediaType.get("application/json"))
    )
    .client(client)
    .build()
    .create(GeocodioService::class.java)

  @Provides @Senate fun providesSenateCWCClient(): OkHttpClient = createOkHttpClient(
    apiKeyName = "apikey",
    apiKey = getEnvironmentVariable(EnvironmentVariable.SCWCApiKey),
  )

  @Provides @Senate fun providesSenateCWCService(
    @Senate client: OkHttpClient
  ): CommunicateWithCongressService = Retrofit.Builder()
    .baseUrl(getEnvironmentVariable(EnvironmentVariable.SCWCUrl))
    .addConverterFactory(
      XML { xmlDeclMode = XmlDeclMode.Charset }.asConverterFactory(MediaType.get("application/xml"))
    )
    .client(client)
    .build()
    .create(CommunicateWithCongressService::class.java)
}