/**
 * Created by smallufo on 2017-04-08.
 */
package destiny.core.astrology

import mu.KotlinLogging
import java.util.*
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertSame
import kotlin.test.assertTrue


class LunarApsisTest {

  private val logger = KotlinLogging.logger { }

  @Test
  fun testToString() {

    for (each in LunarApsis.values) {
      assertNotNull(each)
      assertNotNull(each.toString())
      logger.info("{}", each.toString())
    }

    val set = LunarApsis.values.map { it.toString(Locale.TAIWAN) }.toSet()
    assertTrue(set.contains("遠地點"))
    assertTrue(set.contains("近地點"))
  }

  @Test
  fun testStringConvert() {
    LunarApsis.values.forEach { star ->
      logger.info { "$star = ${star::class.simpleName}" }
      assertSame(star, LunarApsis.fromString(star::class.simpleName!!))
    }
  }

}
