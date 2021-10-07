package org.climatechangemakers.act.feature.communicatewithcongress.manager

import org.climatechangemakers.act.feature.action.model.SendEmailRequest

interface CommunicateWithCongressManager {

  /**
   * Send emails to multiple members of congress as specified in [SendEmailRequest.contactedBioguideIds].
   */
  suspend fun sendEmails(request: SendEmailRequest)
}