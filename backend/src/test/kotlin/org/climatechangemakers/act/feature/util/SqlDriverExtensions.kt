package org.climatechangemakers.act.feature.util

import com.squareup.sqldelight.db.SqlDriver
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
): Long {
  val cursor = executeQuery(0, "INSERT INTO issue(title, precomposed_tweet_template, image_url, description, is_active) VALUES(?,?,?,?,?) RETURNING id;", 4) {
    bindString(1, title)
    bindString(2, precomposedTweet)
    bindString(3, imageUrl)
    bindString(4, description)
    bindLong(5, if (isActive) 1 else 0)
  }

  return checkNotNull(cursor.also { it.next() }.getLong(0))
}

fun SqlDriver.insertMemberOfCongress(member: MemberOfCongress) = execute(
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
    |  cwc_office_code
    |)
    |VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)
  """.trimMargin(),
  parameters = 9,
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
