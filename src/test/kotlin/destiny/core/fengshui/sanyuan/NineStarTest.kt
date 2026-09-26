/**
 * Created by smallufo on 2019-11-23.
 */
package destiny.core.fengshui.sanyuan

import destiny.core.fengshui.sanyuan.NineStar.*
import kotlin.test.Test
import destiny.tools.Lang
import destiny.tools.getDescription
import destiny.tools.getTitle
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertSame

class NineStarTest {

  @Test
  fun testLoop_next() {
    assertSame(貪狼 ,  貪狼.next(0))
    assertSame(巨門 ,  貪狼.next)
    assertSame(巨門 ,  貪狼.next(1))
    assertSame(祿存 ,  貪狼.next(2))
    assertSame(文曲 ,  貪狼.next(3))
    assertSame(廉貞 ,  貪狼.next(4))
    assertSame(武曲 ,  貪狼.next(5))
    assertSame(破軍 ,  貪狼.next(6))
    assertSame(左輔 ,  貪狼.next(7))
    assertSame(右弼 ,  貪狼.next(8))

    assertSame(貪狼 ,  貪狼.next(9))
    assertSame(右弼 ,  貪狼.next(17))
    assertSame(貪狼 ,  貪狼.next(18))
    assertSame(貪狼 ,  貪狼.next(180))

    assertSame(右弼 ,  右弼.next(0))
    assertSame(貪狼 ,  右弼.next)
    assertSame(貪狼 ,  右弼.next(1))
    assertSame(巨門 ,  右弼.next(2))
    assertSame(祿存 ,  右弼.next(3))
    assertSame(文曲 ,  右弼.next(4))
    assertSame(廉貞 ,  右弼.next(5))
    assertSame(武曲 ,  右弼.next(6))
    assertSame(破軍 ,  右弼.next(7))
    assertSame(左輔 ,  右弼.next(8))

    assertSame(右弼 ,  右弼.next(9))
    assertSame(貪狼 ,  右弼.next(10))
    assertSame(右弼 ,  右弼.next(18))
    assertSame(右弼 ,  右弼.next(180))
  }

  @Test
  fun testLoop_prev() {
    assertSame(貪狼 ,  貪狼.prev(0))
    assertSame(右弼 ,  貪狼.prev)
    assertSame(右弼 ,  貪狼.prev(1))
    assertSame(左輔 ,  貪狼.prev(2))
    assertSame(破軍 ,  貪狼.prev(3))
    assertSame(武曲 ,  貪狼.prev(4))
    assertSame(廉貞 ,  貪狼.prev(5))
    assertSame(文曲 ,  貪狼.prev(6))
    assertSame(祿存 ,  貪狼.prev(7))
    assertSame(巨門 ,  貪狼.prev(8))

    assertSame(貪狼 ,  貪狼.prev(9))
    assertSame(右弼 ,  貪狼.prev(10))
    assertSame(貪狼 ,  貪狼.prev(18))
    assertSame(貪狼 ,  貪狼.prev(180))

    assertSame(右弼 ,  右弼.prev(0))
    assertSame(左輔 ,  右弼.prev)
    assertSame(左輔 ,  右弼.prev(1))
    assertSame(破軍 ,  右弼.prev(2))
    assertSame(武曲 ,  右弼.prev(3))
    assertSame(廉貞 ,  右弼.prev(4))
    assertSame(文曲 ,  右弼.prev(5))
    assertSame(祿存 ,  右弼.prev(6))
    assertSame(巨門 ,  右弼.prev(7))
    assertSame(貪狼 ,  右弼.prev(8))

    assertSame(右弼 ,  右弼.prev(9))
    assertSame(左輔 ,  右弼.prev(10))
    assertSame(右弼 ,  右弼.prev(18))
    assertSame(右弼 ,  右弼.prev(180))
  }

  @Test
  fun testIntToStar() {
    with(NineStar) {
      assertSame(貪狼, 1.toStar())
      assertSame(巨門, 2.toStar())
      assertSame(祿存, 3.toStar())
      assertSame(文曲, 4.toStar())
      assertSame(廉貞, 5.toStar())
      assertSame(武曲, 6.toStar())
      assertSame(破軍, 7.toStar())
      assertSame(左輔, 8.toStar())
      assertSame(右弼, 9.toStar())

      assertSame(貪狼, 10.toStar())
      assertSame(右弼, 18.toStar())
      assertSame(貪狼, 19.toStar())
      assertSame(右弼, 27.toStar())
      assertSame(右弼, 270.toStar())

      assertSame(右弼, 0.toStar())
      assertSame(左輔, (-1).toStar())
      assertSame(貪狼, (-8).toStar())
      assertSame(右弼, (-9).toStar())
      assertSame(貪狼, (-17).toStar())
      assertSame(右弼, (-18).toStar())
      assertSame(右弼, (-180).toStar())
    }
  }

  /**
   * title 是玄空飛星名（風水用），description 是九星気学名（matching 報告用）。
   *
   * 🔴 `getDescription` 查不到 key 會退回 `getTitle` —— 少了任何一行，
   * 「貪狼」就會跑進日文報告（而日本讀者會把它當成紫微斗数的貪狼）。
   */
  @Test
  fun description_isKigakuName_perLang() {
    val expected = mapOf(
      Lang.ZH_TW to "一白水星 二黑土星 三碧木星 四綠木星 五黃土星 六白金星 七赤金星 八白土星 九紫火星",
      Lang.JA    to "一白水星 二黒土星 三碧木星 四緑木星 五黄土星 六白金星 七赤金星 八白土星 九紫火星",
      Lang.ZH_CN to "一白水星 二黑土星 三碧木星 四绿木星 五黄土星 六白金星 七赤金星 八白土星 九紫火星",
    )
    expected.forEach { (lang, names) ->
      assertEquals(names.split(" "), NineStar.entries.map { it.getDescription(lang) }, "lang=$lang")
      NineStar.entries.forEach { star ->
        assertNotEquals(star.getTitle(lang), star.getDescription(lang), "$star lang=$lang 的 description 退回了 title")
      }
    }
  }

  /** 繁中的 title 必須等於 enum 名 —— 既有的風水呼叫端靠它 */
  @Test
  fun title_zhTW_isEnumName() {
    NineStar.entries.forEach { assertEquals(it.name, it.getTitle(Lang.ZH_TW)) }
  }


}
