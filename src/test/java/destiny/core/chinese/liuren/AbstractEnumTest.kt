/**
 * Created by smallufo on 2021-09-25.
 */
package destiny.core.chinese.liuren

import destiny.tools.getDescription
import destiny.tools.getTitle
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.elementNames
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import mu.KotlinLogging
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.test.Test
import kotlin.test.assertNotNull

/**
 * Reflection: Access enum values and valueOf via KClass
 * https://youtrack.jetbrains.com/issue/KT-14743
 */

fun KType.enumValueOf(name: String, serializer: KSerializer<Any?> = serializer(this)): Enum<*> {
  if (serializer.descriptor.kind != SerialKind.ENUM) throw error("enumValueOf must be used on enum")
  return Json.decodeFromString(serializer, "\"$name\"") as Enum<*>
}

fun KType.enumValuesName(serializer: KSerializer<Any?> = serializer(this)): List<String> {
  if (serializer.descriptor.kind != SerialKind.ENUM) throw error("enumValuesName must be used on enum")
  val enumName = serializer.descriptor.serialName
  return serializer.descriptor.elementNames.map { it.removePrefix(enumName) }
}


fun KType.enumValues(serializer: KSerializer<Any?> = serializer(this)): List<Enum<*>> {
  if (serializer.descriptor.kind != SerialKind.ENUM) throw error("enumValues must be used on enum")
  return enumValuesName(serializer).map { enumValueOf(it, serializer) }
}


abstract class AbstractEnumTest<T : Enum<*>>(val t: KType) {

  @Test
  inline fun <reified T : Enum<T>> testLocaleStrings(
    values: List<T>,
    locales: List<Locale> =
      listOf(Locale.TRADITIONAL_CHINESE, Locale.SIMPLIFIED_CHINESE, Locale.ENGLISH)
  ) {

    val logger = KotlinLogging.logger { }

    locales.forEach { locale ->
      values.forEach {
        it.getTitle(locale).also { title ->
          assertNotNull(title)
          logger.info { "${it.name} : title($locale) = $title" }
        }

        it.getDescription(locale).also { desc ->
          assertNotNull(desc)
          logger.info { "${it.name} : description($locale) = $desc" }
        }
      }
    }
  }
}

abstract class EnumTest {

  inline fun <reified T : Enum<T>> getEnumValues(enumClass: KClass<out Enum<T>>): Array<out Enum<T>> = enumClass.java.enumConstants

  inline fun <reified E : Enum<E>> testEnums(
    kClass: KClass<out Enum<E>>, locales: List<Locale> =
      listOf(Locale.TRADITIONAL_CHINESE, Locale.SIMPLIFIED_CHINESE, Locale.ENGLISH)
  ) {

    val logger = KotlinLogging.logger { }

    locales.forEach { locale ->
      getEnumValues(kClass).forEach {
        it.getTitle(locale).also { title ->
          assertNotNull(title)
          logger.info { "${it.name} : title($locale) = $title" }
        }

        it.getDescription(locale).also { desc ->
          assertNotNull(desc)
          logger.info { "${it.name} : description($locale) = $desc" }
        }
      }
    }
  }

}
