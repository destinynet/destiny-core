/**
 * @author smallufo
 * Created on 2008/1/16 at 上午 12:21:43
 */
package destiny.core.astrology

import destiny.core.AbstractPointTest
import destiny.core.getAbbreviation
import kotlinx.serialization.json.Json
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class FixedStarTest : AbstractPointTest(FixedStar::class) {

  @Test
  fun testSerialize() {
    FixedStar.values.forEach { p ->
      val rawJson = Json.encodeToString(p)
      assertEquals("\"${p.nameKey}\"", rawJson)
      assertSame(p, Json.decodeFromString(rawJson))
    }
  }

  @Test
  fun testDefaultList() {
    assertEquals(19, FixedStar.defaultList.size)
    assertEquals(38, FixedStar.list.size)
    // 預設組是 values 開頭那 19 顆，順序相同（下游的 points 集合依此順序組成）
    assertEquals(FixedStar.list.take(19), FixedStar.defaultList)
  }

  @Test
  fun testNature() {
    FixedStar.list.forEach { assertTrue(it.nature.size in 1..2, "$it") }
    assertEquals(listOf(Planet.MARS, Planet.JUPITER), FixedStar.REGULUS.nature)
    assertEquals(listOf(Planet.SATURN, Planet.JUPITER), FixedStar.ALGOL.nature)
  }

  @Test
  fun testNewStarNames() {
    assertEquals("南門二", FixedStar.TOLIMAN.getTitle(Locale.TAIWAN))
    assertEquals("南门二", FixedStar.TOLIMAN.getTitle(Locale.SIMPLIFIED_CHINESE))
    assertEquals("Toliman", FixedStar.TOLIMAN.getTitle(Locale.ENGLISH))
    assertEquals("門", FixedStar.TOLIMAN.getAbbreviation(Locale.TAIWAN))
    assertEquals("Tol", FixedStar.TOLIMAN.getAbbreviation(Locale.ENGLISH))
  }

  @Test
  fun testToStringLocale() {

    assertEquals("畢宿五", FixedStar.ALDEBARAN.getTitle(Locale.TAIWAN))
    assertEquals("毕宿五", FixedStar.ALDEBARAN.getTitle(Locale.SIMPLIFIED_CHINESE))
    assertEquals("Aldebaran", FixedStar.ALDEBARAN.getTitle(Locale.ENGLISH))
    //assertEquals("Aldebaran", FixedStar.ALDEBARAN.getTitle(Locale.FRANCE))

    assertEquals("畢", FixedStar.ALDEBARAN.getAbbreviation(Locale.TAIWAN))
    assertEquals("毕", FixedStar.ALDEBARAN.getAbbreviation(Locale.SIMPLIFIED_CHINESE))
    assertEquals("Ald", FixedStar.ALDEBARAN.getAbbreviation(Locale.ENGLISH))
    //assertEquals("Ald", FixedStar.ALDEBARAN.getAbbreviation(Locale.FRANCE))

  }

}
