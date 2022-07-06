/**
 * Created by smallufo on 2021-07-19.
 */
package destiny.tools.serializers

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import mu.KotlinLogging
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

internal class LocaleSerializerTest {

  val logger = KotlinLogging.logger { }

  @Test
  fun testEncodeDecode() {
    val module = SerializersModule {
      contextual(LocaleSerializer)
    }

    val format = Json { serializersModule = module }

    format.encodeToString(Locale.TAIWAN).also { raw ->
      assertEquals(""""zh-TW"""", raw)

      assertEquals(Locale.TAIWAN, format.decodeFromString<Locale>(raw))
    }
  }
}
