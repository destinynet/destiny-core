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

  val service = HazardService()

  @Test
  fun testGetChildHazards() {
    val ew = EightWords("壬寅", "丁未", "辛未", "己丑")
    val locales = listOf(Locale.TAIWAN, Locale.SIMPLIFIED_CHINESE)
    service.getChildHazards(ew, null).forEach { (hazard, book) ->
      locales.forEach { locale ->
        logger.info { "${hazard.getName(locale)} 《$book》 ${hazard.getBookNote(locale, book)}" }
      }
    }
  }

  @Test
  fun testGetChildHazardNotes() {
    val ew = EightWords("壬寅", "丁未", "辛未", "己丑")
    val locales = listOf(Locale.TAIWAN, Locale.SIMPLIFIED_CHINESE)
    locales.forEach { locale ->
      service.getChildHazardNotes(ew, null, locale).forEach { (title , note) ->
        logger.info { "$title : $note" }
      }
    }
  }
}
