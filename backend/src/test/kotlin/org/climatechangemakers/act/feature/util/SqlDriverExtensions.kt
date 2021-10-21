package org.climatechangemakers.act.feature.util

import com.squareup.sqldelight.db.SqlDriver
import org.climatechangemakers.act.common.model.RepresentedArea
import org.climatechangemakers.act.feature.findlegislator.model.LegislatorPoliticalParty
import org.climatechangemakers.act.feature.findlegislator.model.LegislatorRole
import org.climatechangemakers.act.feature.findlegislator.model.MemberOfCongress

fun SqlDriver.insertIssue(
  id: Long,
  title: String,
  precomposedTweet: String,
  imageUrl: String,
  description: String = "description",
) {
  execute(0, "INSERT INTO issue(id, title, precomposed_tweet_template, image_url, description) VALUES(?,?,?,?,?)", 2) {
    bindLong(1, id)
    bindString(2, title)
    bindString(3, precomposedTweet)
    bindString(4, imageUrl)
    bindString(5, description)
  }
}

fun SqlDriver.insertMemberOfCongress(member: MemberOfCongress) = execute(
  0, "INSERT INTO member_of_congress VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)", 9
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
