package org.climatechangemakers.act.feature.cms.manager.auth

interface UserVerificationManager {

  /**
   * Verify that the [username] and [password] is a valid combination
   * and can be authenticated.
   */
  suspend fun verifyLogin(username: String, password: String): Boolean
}