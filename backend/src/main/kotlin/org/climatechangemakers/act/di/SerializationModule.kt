package org.climatechangemakers.act.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json
import nl.adaptivity.xmlutil.XmlDeclMode
import nl.adaptivity.xmlutil.serialization.XML
import okhttp3.MediaType
import retrofit2.Converter

@Module object SerializationModule {

  @Provides fun providesJson(): Json = Json {
    prettyPrint = true
  }

  @Provides fun providesJsonConverterFactory(): Converter.Factory = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
  }.asConverterFactory(MediaType.get("application/json"))

  @Provides fun providesXml(): XML = XML {
    indentString = "  "
    xmlDeclMode = XmlDeclMode.Charset
  }
}