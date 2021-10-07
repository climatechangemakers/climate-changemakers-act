package org.climatechangemakers.act.common.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.text.SimpleDateFormat
import java.util.Date

object DateSerializer : KSerializer<Date> {
  override val descriptor = PrimitiveSerialDescriptor("Date", PrimitiveKind.STRING)
  override fun serialize(encoder: Encoder, value: Date) = encoder.encodeString(value.toEightCharFormat())
  override fun deserialize(decoder: Decoder): Date = TODO()
}

fun Date.toEightCharFormat(): String = SimpleDateFormat("yyyyMMdd").format(this)
