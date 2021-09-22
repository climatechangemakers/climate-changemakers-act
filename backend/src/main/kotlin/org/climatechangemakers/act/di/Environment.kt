package org.climatechangemakers.act.di

fun getEnvironmentVariable(
  key: EnvironmentVariable,
): String = requireNotNull(System.getenv(key.key)) { "No environment variable $key set" }

enum class EnvironmentVariable(val key: String) {
  GeocodioApiKey("GEOCODIO_API_KEY"),
  DatabasePassword("POSTGRES_PASSWORD"),
  DatabaseUser("POSTGRES_USER"),
  DatabaseName("POSTGRES_DB"),
  DatabaseHostname("POSTGRES_HOSTNAME"),
  DatabasePort("POSTGRES_PORT"),
}