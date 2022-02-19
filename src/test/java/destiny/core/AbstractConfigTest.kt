/**
 * Created by smallufo on 2021-08-14.
 */
package destiny.core

import kotlinx.serialization.KSerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import org.junit.jupiter.api.TestInstance
import kotlin.test.Test
import kotlin.test.assertEquals


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class AbstractConfigTest<T> {

  abstract val serializer : KSerializer<T>

  abstract val configByConstructor: T
  abstract val configByFunction: T

  abstract val assertion: (String) -> Unit

  protected val logger = KotlinLogging.logger { }

  private inline fun <T> assertAndCompareDecoded(format: Json, config: T, assertion: (String) -> Unit, serializer: KSerializer<T>) {
    val raw = format.encodeToString(serializer, config)
    val logger = KotlinLogging.logger { }
    logger.info { raw }
    assertion(raw)
    val decoded: T = format.decodeFromString(serializer, raw)
    assertEquals(config, decoded)
  }

  private inline fun <reified T> assertAndCompareDecoded(format: Json, config: T, assertion: (String) -> Unit) {
    val raw = format.encodeToString(config)
    assertion(raw)
    val decoded = format.decodeFromString<T>(raw)
    assertEquals(config, decoded)
  }

  private val prettyJson = Json {
    encodeDefaults = true
    prettyPrint = true
  }

  private val condenseJson = Json {
    encodeDefaults = true
    prettyPrint = false
  }

  @Test
  fun testEquals() {
    assertEquals(configByConstructor, configByFunction)
  }

  @Test
  fun testSerialize() {
    assertAndCompareDecoded(prettyJson, configByConstructor, assertion, serializer)
    assertAndCompareDecoded(condenseJson, configByFunction, assertion, serializer)
  }
}


