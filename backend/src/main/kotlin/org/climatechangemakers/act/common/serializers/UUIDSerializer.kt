package org.climatechangemakers.act.common.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.UUID

object UUIDSerializer : KSerializer<UUID> {
  override val descriptor = PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)
  override fun serialize(encoder: Encoder, value: UUID) = encoder.encodeString(value.toAlphaNumericString())
  override fun deserialize(decoder: Decoder): UUID = TODO()
}

fun UUID.toAlphaNumericString(): String = toString().filter { it != '-' }
