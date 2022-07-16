/**
 * Created by smallufo on 2022-07-17.
 */
package destiny.core.chinese.eightwords.hazards

import destiny.core.calendar.eightwords.EightWords
import mu.KotlinLogging
import java.util.*
import kotlin.test.Test

internal class HazardServiceTest {

  val logger = KotlinLogging.logger { }

  @Test
  fun test() {
    val service = HazardService()
    val ew = EightWords("壬寅", "丁未", "辛未", "己丑")
    service.test(ew, null).forEach { (hazard, book) ->
      logger.info { "${hazard.getName()} 《$book》 ${hazard.getNote(Locale.TAIWAN, book)}" }
    }

  }
}
