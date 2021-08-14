/**
 * Created by smallufo on 2021-08-14.
 */
package destiny.core

import destiny.core.calendar.eightwords.assertAndCompareDecoded
import kotlinx.serialization.KSerializer
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

  @Test
  fun testEquals() {
    assertEquals(configByConstructor, configByFunction)
  }

  @Test
  fun testSerialize() {
    Json {
      encodeDefaults = true
      prettyPrint = true
    }.also { format ->
      assertAndCompareDecoded(format, configByConstructor, assertion, serializer)
    }

    Json {
      encodeDefaults = true
      prettyPrint = false
    }.also { format ->
      assertAndCompareDecoded(format, configByFunction, assertion, serializer)
    }
  }
}
