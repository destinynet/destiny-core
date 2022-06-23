/**
 * Created by smallufo on 2017-04-11.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch.*
import destiny.core.chinese.Stem.*
import destiny.core.chinese.ziwei.StarUnlucky.Companion.fun地劫
import destiny.core.chinese.ziwei.StarUnlucky.Companion.fun地空
import destiny.core.chinese.ziwei.StarUnlucky.Companion.fun擎羊
import destiny.core.chinese.ziwei.StarUnlucky.Companion.fun火星_全集
import destiny.core.chinese.ziwei.StarUnlucky.Companion.fun鈴星_全集
import destiny.core.chinese.ziwei.StarUnlucky.Companion.fun陀羅
import destiny.core.getAbbreviation
import destiny.core.toString
import mu.KotlinLogging
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertSame

class StarUnluckyTest {

  private val logger = KotlinLogging.logger { }

  @Test
  fun testToString() {
    assertEquals("鈴星" , StarUnlucky.鈴星.toString(Locale.TAIWAN))
    assertEquals("铃星" , StarUnlucky.鈴星.toString(Locale.SIMPLIFIED_CHINESE))
    assertEquals("鈴星" , StarUnlucky.鈴星.toString(Locale.ENGLISH))
  }

  @Test
  fun testAbbr() {
    assertEquals("鈴" , StarUnlucky.鈴星.getAbbreviation(Locale.TAIWAN))
    assertEquals("铃" , StarUnlucky.鈴星.getAbbreviation(Locale.SIMPLIFIED_CHINESE))
    assertEquals("鈴" , StarUnlucky.鈴星.getAbbreviation(Locale.ENGLISH))
  }

  @Test
  fun testToStrings() {
    for (star in StarUnlucky.values) {
      assertNotNull(star.toString())
      assertNotNull(star.toString(Locale.TAIWAN))
      assertNotNull(star.toString(Locale.CHINA))
      logger.info("tw = {}({}) , cn = {}({})",
                  star.toString(Locale.TAIWAN), star.getAbbreviation(Locale.TAIWAN),
                  star.toString(Locale.CHINA), star.getAbbreviation(Locale.CHINA))
    }
  }

  @Test
  fun test擎羊() {
    assertSame(卯, fun擎羊.invoke(甲))
    assertSame(辰, fun擎羊.invoke(乙))
    assertSame(午, fun擎羊.invoke(丙))
    assertSame(未, fun擎羊.invoke(丁))
    assertSame(午, fun擎羊.invoke(戊))
    assertSame(未, fun擎羊.invoke(己))
    assertSame(酉, fun擎羊.invoke(庚))
    assertSame(戌, fun擎羊.invoke(辛))
    assertSame(子, fun擎羊.invoke(壬))
    assertSame(丑, fun擎羊.invoke(癸))
  }

  @Test
  fun test陀羅() {
    assertSame(丑, fun陀羅.invoke(甲))
    assertSame(寅, fun陀羅.invoke(乙))
    assertSame(辰, fun陀羅.invoke(丙))
    assertSame(巳, fun陀羅.invoke(丁))
    assertSame(辰, fun陀羅.invoke(戊))
    assertSame(巳, fun陀羅.invoke(己))
    assertSame(未, fun陀羅.invoke(庚))
    assertSame(申, fun陀羅.invoke(辛))
    assertSame(戌, fun陀羅.invoke(壬))
    assertSame(亥, fun陀羅.invoke(癸))
  }

  @Test
  fun test火星() {
    assertSame(子, fun火星_全集.invoke(午, 亥))
    assertSame(卯, fun火星_全集.invoke(申, 丑))
    assertSame(辰, fun火星_全集.invoke(丑, 丑))
    assertSame(寅, fun火星_全集.invoke(未, 巳))
  }

  @Test
  fun test鈴星() {
    assertSame(丑, fun鈴星_全集.invoke(戌, 戌))
    assertSame(午, fun鈴星_全集.invoke(申, 申))
    assertSame(卯, fun鈴星_全集.invoke(巳, 巳))
    assertSame(丑, fun鈴星_全集.invoke(卯, 卯))
  }

  @Test
  fun test地劫() {
    assertSame(亥, fun地劫.invoke(子))
    assertSame(子, fun地劫.invoke(丑))
    assertSame(丑, fun地劫.invoke(寅))
    assertSame(寅, fun地劫.invoke(卯))
    assertSame(卯, fun地劫.invoke(辰))
    assertSame(辰, fun地劫.invoke(巳))
    assertSame(巳, fun地劫.invoke(午))
    assertSame(午, fun地劫.invoke(未))
    assertSame(未, fun地劫.invoke(申))
    assertSame(申, fun地劫.invoke(酉))
    assertSame(酉, fun地劫.invoke(戌))
    assertSame(戌, fun地劫.invoke(亥))
  }

  @Test
  fun test地空() {
    assertSame(亥, fun地空.invoke(子))
    assertSame(戌, fun地空.invoke(丑))
    assertSame(酉, fun地空.invoke(寅))
    assertSame(申, fun地空.invoke(卯))
    assertSame(未, fun地空.invoke(辰))
    assertSame(午, fun地空.invoke(巳))
    assertSame(巳, fun地空.invoke(午))
    assertSame(辰, fun地空.invoke(未))
    assertSame(卯, fun地空.invoke(申))
    assertSame(寅, fun地空.invoke(酉))
    assertSame(丑, fun地空.invoke(戌))
    assertSame(子, fun地空.invoke(亥))
  }
}
