package org.climatechangemakers.act.feature.communicatewithcongress.manager

import org.climatechangemakers.act.common.model.Result
import org.climatechangemakers.act.feature.action.model.SendEmailRequest

interface CommunicateWithCongressManager {

  /**
   * Send emails to multiple members of congress as specified in [SendEmailRequest.contactedBioguideIds].
   * This function returns either the email body that was sent, or a list of failed bioguide IDs.
   */
  suspend fun sendEmails(request: SendEmailRequest): Result<String, List<String>>
}