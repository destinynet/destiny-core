/**
 * Created by smallufo on 2021-09-23.
 */
package destiny.core.chinese

import mu.KotlinLogging
import java.util.*
import kotlin.test.Test
import kotlin.test.assertNotNull

internal class TianyiTest {

  private val logger = KotlinLogging.logger { }

  @Test
  fun testDescriptive() {

    val locales = listOf(Locale.TRADITIONAL_CHINESE, Locale.SIMPLIFIED_CHINESE, Locale.ENGLISH)

    Tianyi.values().forEach { tianyi: Tianyi ->

      locales.forEach { locale ->
        tianyi.asDescriptive().toString(locale).also {
          assertNotNull(it)
          logger.info { "$tianyi title($locale) = '$it'" }
        }

        tianyi.asDescriptive().getDescription(locale).also {
          assertNotNull(it)
          logger.info { "$tianyi description($locale) = '$it'" }
        }
      }
    }
  }
}
