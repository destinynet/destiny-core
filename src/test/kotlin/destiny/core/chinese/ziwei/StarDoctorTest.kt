/**
 * Created by smallufo on 2022-02-16.
 */
package destiny.core.chinese.ziwei

import destiny.core.AbstractPointTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.Json.Default.decodeFromString
import kotlin.test.Test
import kotlin.test.assertSame

internal class StarDoctorTest : AbstractPointTest(StarDoctor::class) {

  @Test
  fun testSerialize() {
    StarDoctor.values.forEach { s ->
      val rawJson = Json.encodeToString(s)
      logger.info { "$s = $rawJson" }
      assertSame(s, decodeFromString(rawJson))
    }
  }
}
