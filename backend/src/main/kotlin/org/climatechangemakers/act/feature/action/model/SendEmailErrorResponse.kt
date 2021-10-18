package org.climatechangemakers.act.feature.action.model

import kotlinx.serialization.Serializable

@Serializable class SendEmailErrorResponse(val failedBioguideIds: List<String>)