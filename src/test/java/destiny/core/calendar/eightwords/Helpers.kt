/**
 * Created by smallufo on 2021-08-11.
 */
package destiny.core.calendar.eightwords

import kotlinx.serialization.KSerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.assertEquals

inline fun <T> assertAndCompareDecoded(format: Json, config: T, assertion: (String) -> Unit, serializer: KSerializer<T>) {
  val raw = format.encodeToString(serializer, config)
  assertion(raw)
  val decoded: T = format.decodeFromString(serializer, raw)
  assertEquals(config , decoded)
}

inline fun <reified T> assertAndCompareDecoded(format: Json, config: T, assertion: (String) -> Unit) {
  val raw = format.encodeToString(config)
  assertion(raw)
  val decoded = format.decodeFromString<T>(raw)
  assertEquals(config, decoded)
}
