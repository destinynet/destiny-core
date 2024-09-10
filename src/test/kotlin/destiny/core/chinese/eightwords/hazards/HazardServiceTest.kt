/**
 * Created by smallufo on 2022-07-17.
 */
package destiny.core.chinese.eightwords.hazards

import destiny.core.calendar.eightwords.EightWords
import destiny.core.chinese.eightwords.hazards.ChildHazard.*
import destiny.tools.KotlinLogging
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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
      service.getChildHazardNotes(ew, null, locale).also { items ->
        assertEquals(5, items.size)
        assertTrue { items.map { it.hazard }.containsAll(setOf(千日關, 水火關, 白虎關, 金鎖關, 撞命關)) }
      }.forEach { item ->
        logger.info { "$item" }
      }
    }
  }
}
