/**
 * Created by smallufo on 2021-08-11.
 */
package destiny.core.calendar.eightwords

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.assertEquals

inline fun <reified T> assertAndCompareDecoded(format: Json, config: T, assertion: (String) -> Unit) {
  val raw = format.encodeToString(config)
  assertion(raw)
  val decoded = format.decodeFromString<T>(raw)
  assertEquals(config, decoded)
}
