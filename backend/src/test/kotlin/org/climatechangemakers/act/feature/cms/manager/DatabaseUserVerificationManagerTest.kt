package org.climatechangemakers.act.feature.cms.manager

import app.cash.sqldelight.db.SqlDriver
import okio.ByteString
import org.climatechangemakers.act.database.Database
import org.climatechangemakers.act.feature.findlegislator.util.suspendTest
import org.climatechangemakers.act.feature.util.TestContainerProvider
import org.junit.Test
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DatabaseUserVerificationManagerTest : TestContainerProvider() {

  @Test fun `login verification returns true`() = suspendTest {
    driver.insertHashedCredentials("kevin", "password")
    assertTrue(manager().verifyLogin("kevin", "password"))
  }

  @Test fun `login verification returns false wrong password`() = suspendTest {
    driver.insertHashedCredentials("kevin", "password")
    assertFalse(manager().verifyLogin("kevin", "wrong password!"))
  }

  @Test fun `login verification returns false wrong username`() = suspendTest {
    driver.insertHashedCredentials("kevin", "password")
    assertFalse(manager().verifyLogin("not_kevin", "password"))
  }

  private fun manager(
    coroutineContext: CoroutineContext = EmptyCoroutineContext,
    database: Database = this.database
  ) = DatabaseUserVerificationManager(database, coroutineContext)
}

private fun SqlDriver.insertHashedCredentials(
  username: String,
  password: String,
) = execute(
  identifier = 0,
  sql = "INSERT INTO content_management_user(user_name, password_sha_512) VALUES (?, ?);",
  parameters = 2,
) {
  bindString(0, username)
  bindString(1, ByteString.encodeUtf8("$username$password").sha512().hex())
}