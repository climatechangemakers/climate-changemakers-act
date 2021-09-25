package org.climatechangemakers.act.common.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

interface StringEnum {
  val value: String
}

abstract class StringEnumSerializer<T>(
  private val enumValues: Array<out T>,
) : KSerializer<T> where T : StringEnum, T : Enum<T> {

  override val descriptor get() = PrimitiveSerialDescriptor("StringEnumSerializer", PrimitiveKind.STRING)
  override fun serialize(encoder: Encoder, value: T) = encoder.encodeString(value.value)
  override fun deserialize(decoder: Decoder): T = decoder.decodeString().let { value ->
    enumValues.first { it.value == value }
  }
}
