package org.climatechangemakers.act.common.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object YesNoBooleanSerializer : KSerializer<Boolean> {

  override val descriptor = PrimitiveSerialDescriptor("Y/N", PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: Boolean) = encoder.encodeString(
    if (value) "Y" else "N"
  )

  override fun deserialize(decoder: Decoder): Boolean = when (val code = decoder.decodeString()) {
    "Y" -> true
    "N" -> false
    else -> error("Got $code but expected either Y or N")
  }
}