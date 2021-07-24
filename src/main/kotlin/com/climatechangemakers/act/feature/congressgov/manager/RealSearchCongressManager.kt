package com.climatechangemakers.act.feature.congressgov.manager

import javax.inject.Inject

class RealSearchCongressManager @Inject constructor() : SearchCongressManager {

  // TODO(kcianfarini) implement
  override suspend fun getLegislatorImage() = "https://www.congress.gov/img/member/116_rp_va_4_mceachin_a_200.jpg"
}