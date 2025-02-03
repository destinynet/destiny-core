/**
 * Created by smallufo on 2017-06-16.
 */
package destiny.core.chinese.ziwei

import destiny.core.AbstractPointTest
import destiny.core.chinese.Branch.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.Json.Default.decodeFromString
import kotlin.test.Test
import kotlin.test.assertSame

class StarGeneralFrontTest : AbstractPointTest(StarGeneralFront::class) {

  @Test
  fun testSerialize() {
    StarGeneralFront.values.forEach { s ->
      val rawJson = Json.encodeToString(s)
      logger.info { "$s = $rawJson" }
      assertSame(s, decodeFromString(rawJson))
    }
  }

  @Test
  fun testFun() {
    // 酉年，將星[1] 在酉 , 攀鞍[2] 在戌 , 亡神[12] 在 申
    assertSame(酉, StarGeneralFront.fun將星.invoke(酉))
    assertSame(戌, StarGeneralFront.fun攀鞍.invoke(酉))
    assertSame(申, StarGeneralFront.fun亡神.invoke(酉))
  }
}
