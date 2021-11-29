package org.climatechangemakers.act.di

fun getEnvironmentVariable(
  key: EnvironmentVariable,
): String = requireNotNull(System.getenv(key.key)) { "No environment variable ${key.key} set" }

enum class EnvironmentVariable(val key: String) {
  GeocodioApiKey("GEOCODIO_API_KEY"),
  SCWCApiKey("SCWC_API_KEY"),
  SCWCUrl("SCWC_URL"),
  HCWCApiKey("HCWC_API_KEY"),
  HCWCUrl("HCWC_URL"),
  DatabasePassword("POSTGRES_PASSWORD"),
  DatabaseUser("POSTGRES_USER"),
  DatabaseName("POSTGRES_DB"),
  DatabaseHostname("POSTGRES_HOSTNAME"),
  DatabasePort("POSTGRES_PORT"),
  AirtableApiKey("AIRTABLE_API_KEY"),
  AirtableBaseId("AIRTABLE_BASE_ID"),
}