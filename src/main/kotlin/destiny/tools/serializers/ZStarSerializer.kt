/**
 * Created by smallufo on 2021-10-18.
 */
package destiny.tools.serializers

import destiny.core.IPoints
import destiny.core.chinese.ziwei.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import java.util.*
import kotlin.reflect.KClass

abstract class AbstractStarSerializer<T : ZStar> : KSerializer<T> {
  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ZStar", PrimitiveKind.STRING)

  protected abstract val companion: IPoints<T>

  override fun serialize(encoder: Encoder, value: T) {
    encoder.encodeString("${companion.type.simpleName}:${value.nameKey}")
  }

  override fun deserialize(decoder: Decoder): T {
    val raw = decoder.decodeString()
    val value = raw.substringAfter(":")

    if (!raw.startsWith(companion.type.simpleName!!)) {
      error("Invalid format for ${companion.type.simpleName}: $raw")
    }

    return companion.fromString(value, Locale.getDefault()) ?: error("Cannot deserialize: $raw")
  }
}


object StarMainSerializer : AbstractStarSerializer<StarMain>() {
  override val companion = StarMain
}

object StarLuckySerializer : AbstractStarSerializer<StarLucky>() {
  override val companion = StarLucky
}

object StarUnluckySerializer : AbstractStarSerializer<StarUnlucky>() {
  override val companion = StarUnlucky
}

object StarMinorSerializer : AbstractStarSerializer<StarMinor>() {
  override val companion = StarMinor
}

object StarDoctorSerializer : AbstractStarSerializer<StarDoctor>() {
  override val companion = StarDoctor
}

object StarGeneralFrontSerializer : AbstractStarSerializer<StarGeneralFront>() {
  override val companion = StarGeneralFront
}

object StarLongevitySerializer : AbstractStarSerializer<StarLongevity>() {
  override val companion = StarLongevity
}

object StarYearFrontSerializer : AbstractStarSerializer<StarYearFront>() {
  override val companion = StarYearFront
}

object ZStarSerializer : KSerializer<ZStar> {

  private val serializers = mapOf(
    StarMain::class to StarMain.serializer(),
    StarMinor::class to StarMinor.serializer(),
    StarLucky::class to StarLucky.serializer(),
    StarUnlucky::class to StarUnlucky.serializer(),
    StarDoctor::class to StarDoctor.serializer(),
    StarGeneralFront::class to StarGeneralFront.serializer(),
    StarLongevity::class to StarLongevity.serializer(),
    StarYearFront::class to StarYearFront.serializer()
  )

  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ZStar", PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: ZStar) {
    val serializer = serializers[value::class.supertypes.first().classifier as KClass<*>] ?: error("No serializer found for ${value::class}")
    @Suppress("UNCHECKED_CAST")
    (serializer as KSerializer<ZStar>).serialize(encoder, value)
  }

  override fun deserialize(decoder: Decoder): ZStar {
    val raw = decoder.decodeString()
    val wrappedValue = "\"$raw\""
    val className = raw.substringBefore(":")
    val serializer = serializers.entries.firstOrNull { it.key.simpleName == className }?.value
      ?: error("No serializer found for $className")
    return Json.decodeFromString(serializer, wrappedValue)
  }
}
