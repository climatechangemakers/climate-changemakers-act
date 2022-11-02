package org.climatechangemakers.act.feature.cms.manager

import kotlinx.coroutines.withContext
import okio.ByteString
import org.climatechangemakers.act.database.Database
import org.climatechangemakers.act.di.Io
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class DatabaseUserVerificationManager @Inject constructor(
  database: Database,
  @Io private val ioContext: CoroutineContext,
) : UserVerificationManager {

  private val contentManagementUserQueries = database.contentManagementUserQueries

  override suspend fun verifyLogin(
    username: String,
    password: String,
  ): Boolean = withContext(ioContext) {
    // Use the username as a salt below by concatenating the two values together.
    val passwordHash = ByteString.encodeUtf8("$username$password").sha512().hex()
    contentManagementUserQueries.userWithPasswordHashExists(
      userName = username,
      passwordSha512 = passwordHash,
    ).executeAsOne()
  }
}