package org.climatechangemakers.act.feature.values.controller

import io.ktor.server.application.ApplicationCall
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respondText
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.Json
import org.climatechangemakers.act.common.model.RepresentedArea
import org.climatechangemakers.act.feature.communicatewithcongress.model.Prefix
import org.climatechangemakers.act.feature.communicatewithcongress.model.PrefixSerializer
import org.climatechangemakers.act.feature.communicatewithcongress.model.Topic
import org.climatechangemakers.act.feature.communicatewithcongress.model.TopicSerializer
import javax.inject.Inject

class ValuesController @Inject constructor(private val json: Json) {

  suspend fun areaValues(call: ApplicationCall) {
    call.respondText(
      contentType = ContentType.Application.Json,
      text = json.encodeToString(ArraySerializer(FullNameAreaSerializer), RepresentedArea.values()),
      status = HttpStatusCode.OK,
    )
  }

  suspend fun libraryOfCongressTopicValues(call: ApplicationCall) {
    call.respondText(
      contentType = ContentType.Application.Json,
      text = json.encodeToString(ArraySerializer(TopicSerializer), Topic.values()),
      status = HttpStatusCode.OK,
    )
  }

  suspend fun prefixValues(call: ApplicationCall) = call.respondText(
    contentType = ContentType.Application.Json,
    text = json.encodeToString(ArraySerializer(PrefixSerializer), Prefix.values()),
    status = HttpStatusCode.OK,
  )
}

private object FullNameAreaSerializer : KSerializer<RepresentedArea> {

  override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Area") {
    element<String>("shortName")
    element<String>("fullName")
  }

  override fun deserialize(decoder: Decoder) = TODO()
  override fun serialize(encoder: Encoder, value: RepresentedArea) = encoder.encodeStructure(descriptor) {
    encodeStringElement(descriptor, 0, value.value)
    encodeStringElement(descriptor, 1, value.fullName)
  }
}