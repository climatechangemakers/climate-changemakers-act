package org.climatechangemakers.act.feature.cms.manager

import at.favre.lib.crypto.bcrypt.BCrypt
import kotlinx.coroutines.withContext
import org.climatechangemakers.act.database.Database
import org.climatechangemakers.act.di.Io
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class DatabaseUserVerificationManager @Inject constructor(
  database: Database,
  @Io private val ioContext: CoroutineContext,
) : UserVerificationManager {

  private val contentManagementUserQueries = database.contentManagementUserQueries
  private val bcrypt = BCrypt.verifyer(BCrypt.Version.VERSION_2Y)

  override suspend fun verifyLogin(
    username: String,
    password: String,
  ): Boolean = withContext(ioContext) {
    contentManagementUserQueries.selectHashForUser(username).executeAsOneOrNull()?.let { hash ->
      bcrypt.verify(password.toCharArray(), hash).verified
    } ?: false
  }
}