package org.climatechangemakers.act.feature.membership.model

import kotlinx.serialization.Serializable

@Serializable class AirtableResponse(val records: List<AirtableRecord>)
@Serializable class AirtableRecord(val id: String)