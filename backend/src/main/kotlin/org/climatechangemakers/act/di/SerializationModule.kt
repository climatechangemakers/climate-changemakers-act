package org.climatechangemakers.act.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json
import nl.adaptivity.xmlutil.XmlDeclMode
import nl.adaptivity.xmlutil.serialization.XML
import okhttp3.MediaType

@Module object SerializationModule {

  @Provides fun providesJson(): Json = Json {
    prettyPrint = true
  }

  @Provides fun providesXml(): XML = XML {
    indentString = "  "
    xmlDeclMode = XmlDeclMode.Charset
  }
}