package org.climatechangemakers.act.feature.findlegislator.manager

import org.climatechangemakers.act.feature.findlegislator.model.Location
import org.climatechangemakers.act.feature.findlegislator.util.suspendTest
import org.climatechangemakers.act.feature.util.DEFAULT_MEMBER_OF_CONGRESS
import org.climatechangemakers.act.feature.util.TestContainerProvider
import org.climatechangemakers.act.feature.util.insertMemberOfCongress
import org.junit.Test
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DatabaseDistrictOfficeManagerTest : TestContainerProvider() {

  private val manager = DatabaseDistrictOfficeManager(database, EmptyCoroutineContext)

  @Test fun `selects closest district office`() = suspendTest {
    driver.insertMemberOfCongress(DEFAULT_MEMBER_OF_CONGRESS.copy(bioguideId = "hello"))
    insert("hello", "867-5309", 0.0, 0.0)
    insert("hello", "867-5310", 10.0, 10.0)

    assertEquals(
      "867-5309",
      manager.getNearestDistrictOfficePhoneNumber("hello", Location(1.0, 1.0))
    )
  }

  @Test fun `defaults to first district office with no other lat long`() = suspendTest {
    driver.insertMemberOfCongress(DEFAULT_MEMBER_OF_CONGRESS.copy(bioguideId = "hello"))
    insert("hello", "867-5309", null, null)
    insert("hello", "867-5310", null, null)

    assertEquals(
      "867-5309",
      manager.getNearestDistrictOfficePhoneNumber("hello", Location(1.0, 1.0))
    )
  }

  @Test fun `null is not considered in proximity calculation`() = suspendTest {
    driver.insertMemberOfCongress(DEFAULT_MEMBER_OF_CONGRESS.copy(bioguideId = "hello"))
    insert("hello", "867-5309", null, null)
    insert("hello", "867-5310", 10.0, -10.0)

    assertEquals(
      "867-5310",
      manager.getNearestDistrictOfficePhoneNumber("hello", Location(1.0, 1.0))
    )
  }

  @Test fun `only return district office for related bioguides`() = suspendTest {
    driver.insertMemberOfCongress(DEFAULT_MEMBER_OF_CONGRESS.copy(bioguideId = "hello"))
    driver.insertMemberOfCongress(DEFAULT_MEMBER_OF_CONGRESS.copy(bioguideId = "goodbye"))
    insert("hello", "867-5309", null, null)
    insert("goodbye", "867-5310", 10.0, -10.0)

    assertEquals(
      "867-5310",
      manager.getNearestDistrictOfficePhoneNumber("goodbye", Location(1.0, 1.0))
    )
  }

  private fun insert(bioguideId: String, phoneNumber: String, lat: Double?, long: Double?) {
    driver.execute(0, "INSERT INTO district_office VALUES(?, ?, ?, ?);", 4) {
      bindString(0, bioguideId)
      bindString(1, phoneNumber)
      bindDouble(2, lat)
      bindDouble(3, long)
    }
  }
}