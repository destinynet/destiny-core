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
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal class HazardServiceTest {

  val logger = KotlinLogging.logger { }

  val service = HazardService()

  /**
   * [HazardService.getChildHazards] 傳回的是 (關煞, 出處書籍) 的**逐本**組合 ——
   * 同一個關煞出現在幾本書就有幾筆，故筆數（12）多於關煞數（5，見 [testGetChildHazardNotes]）。
   *
   * 原本只是把每筆關煞的各語系名稱 log 出來，關煞判定錯了也不會紅。
   */
  @Test
  fun testGetChildHazards() {
    val ew = EightWords("壬寅", "丁未", "辛未", "己丑")

    val actual = service.getChildHazards(ew, null)

    assertEquals(
      listOf(
        千日關 to Book.象吉通書,
        千日關 to Book.生育禮俗,
        千日關 to Book.鰲頭通書,
        千日關 to Book.星平會海,
        水火關 to Book.星平會海,
        水火關 to Book.生育禮俗,
        水火關 to Book.黃曆解秘,
        白虎關 to Book.生育禮俗,
        金鎖關 to Book.星平會海,
        金鎖關 to Book.生育禮俗,
        金鎖關 to Book.黃曆解秘,
        撞命關 to Book.生育禮俗,
      ),
      actual
    )

    // 每一筆都查得到該書的說明文字，且不等於關煞名稱本身
    listOf(Locale.TAIWAN, Locale.SIMPLIFIED_CHINESE).forEach { locale ->
      actual.forEach { (hazard, book) ->
        val note = hazard.getBookNote(locale, book)
        logger.info { "${hazard.getName(locale)} 《$book》 $note" }
        assertNotNull(note, "${hazard.getName(locale)} 《$book》 ($locale)")
        assertNotEquals(hazard.getName(locale), note)
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
