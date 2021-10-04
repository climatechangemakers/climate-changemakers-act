package org.climatechangemakers.act.feature.communicatewithcongress.model

import kotlinx.serialization.Serializable
import org.climatechangemakers.act.common.serializers.StringEnum
import org.climatechangemakers.act.common.serializers.StringEnumSerializer

@Serializable(with = PrefixSerializer::class) enum class Prefix(override val value: String) : StringEnum {
  Mr("Mr."),
  Mrs("Mrs."),
  Miss("Miss"),
  Ms("Ms."),
  Dr("Dr.")
}

object PrefixSerializer : StringEnumSerializer<Prefix>(Prefix.values())
