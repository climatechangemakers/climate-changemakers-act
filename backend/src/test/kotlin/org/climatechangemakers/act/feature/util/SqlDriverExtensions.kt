package org.climatechangemakers.act.feature.util

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.JdbcDriver
import app.cash.sqldelight.driver.jdbc.JdbcPreparedStatement
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import org.climatechangemakers.act.common.model.RepresentedArea
import org.climatechangemakers.act.feature.findlegislator.model.LegislatorPoliticalParty
import org.climatechangemakers.act.feature.findlegislator.model.LegislatorRole
import org.climatechangemakers.act.feature.findlegislator.model.MemberOfCongress

fun SqlDriver.insertIssue(
  title: String,
  precomposedTweet: String,
  imageUrl: String,
  description: String = "description",
  isActive: Boolean = true
): Long = executeQuery(
  identifier = 0,
  sql = "INSERT INTO issue(title, precomposed_tweet_template, image_url, description, is_active) VALUES(?,?,?,?,?) RETURNING id;",
  mapper = { cursor -> cursor.also { it.next() }.getLong(0)!! },
  parameters = 4,
) {
  bindString(1, title)
  bindString(2, precomposedTweet)
  bindString(3, imageUrl)
  bindString(4, description)
  bindLong(5, if (isActive) 1 else 0)
}

fun SqlDriver.insertMemberOfCongress(
  member: MemberOfCongress,
  // This is an optional default field because we don't actually use the term
  // information anywhere in the codebase. It's simply a filtering condition.
  // We default this field to the UNIX epoch, January 1, 1970.
  termEnd: LocalDate = LocalDate(year = 1970, monthNumber = 1, dayOfMonth = 1),
) = execute(
  identifier = 0,
  sql = """
    |INSERT INTO member_of_congress (
    |  bioguide_id,
    |  full_name,
    |  legislative_role,
    |  state,
    |  congressional_district,
    |  party,
    |  dc_phone_number,
    |  twitter_handle,
    |  cwc_office_code,
    |  term_end,
    |  first_name,
    |  last_name
    |)
    |VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
  """.trimMargin(),
  parameters = 12,
) {
  bindString(1, member.bioguideId)
  bindString(2, member.fullName)
  bindString(3, member.legislativeRole.value)
  bindString(4, member.representedArea.value)
  bindLong(5, member.congressionalDistrict?.toLong())
  bindString(6, member.party.value)
  bindString(7, member.dcPhoneNumber)
  bindString(8, member.twitterHandle)
  bindString(9, member.cwcOfficeCode)
  (this as JdbcPreparedStatement).bindObject(10, termEnd.toJavaLocalDate())
  bindString(11, "")
  bindString(12, "")
}

val DEFAULT_MEMBER_OF_CONGRESS = MemberOfCongress(
  bioguideId = "bioguide",
  fullName = "fullname",
  legislativeRole = LegislatorRole.Representative,
  RepresentedArea.Virginia,
  congressionalDistrict = 1,
  party = LegislatorPoliticalParty.Democrat,
  dcPhoneNumber = "8675309",
  twitterHandle = "handle",
  cwcOfficeCode = "HVA01",
)
