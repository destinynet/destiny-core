/**
 * Created by smallufo on 2017-04-11.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch.*
import destiny.core.chinese.Stem.*
import destiny.core.chinese.ziwei.StarUnlucky.*
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Test
import org.slf4j.LoggerFactory
import java.util.*

class StarUnluckyTest {

  private val logger = LoggerFactory.getLogger(javaClass)

  @Test
  fun testToString() {
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
    assertSame(卯, fun擎羊.apply(甲))
    assertSame(辰, fun擎羊.apply(乙))
    assertSame(午, fun擎羊.apply(丙))
    assertSame(未, fun擎羊.apply(丁))
    assertSame(午, fun擎羊.apply(戊))
    assertSame(未, fun擎羊.apply(己))
    assertSame(酉, fun擎羊.apply(庚))
    assertSame(戌, fun擎羊.apply(辛))
    assertSame(子, fun擎羊.apply(壬))
    assertSame(丑, fun擎羊.apply(癸))
  }

  @Test
  fun test陀羅() {
    assertSame(丑, fun陀羅.apply(甲))
    assertSame(寅, fun陀羅.apply(乙))
    assertSame(辰, fun陀羅.apply(丙))
    assertSame(巳, fun陀羅.apply(丁))
    assertSame(辰, fun陀羅.apply(戊))
    assertSame(巳, fun陀羅.apply(己))
    assertSame(未, fun陀羅.apply(庚))
    assertSame(申, fun陀羅.apply(辛))
    assertSame(戌, fun陀羅.apply(壬))
    assertSame(亥, fun陀羅.apply(癸))
  }

  @Test
  fun test火星() {
    assertSame(子, fun火星_全集.apply(午, 亥))
    assertSame(卯, fun火星_全集.apply(申, 丑))
    assertSame(辰, fun火星_全集.apply(丑, 丑))
    assertSame(寅, fun火星_全集.apply(未, 巳))
  }

  @Test
  fun test鈴星() {
    assertSame(丑, fun鈴星_全集.apply(戌, 戌))
    assertSame(午, fun鈴星_全集.apply(申, 申))
    assertSame(卯, fun鈴星_全集.apply(巳, 巳))
    assertSame(丑, fun鈴星_全集.apply(卯, 卯))
  }

  @Test
  fun test地劫() {
    assertSame(亥, fun地劫.apply(子))
    assertSame(子, fun地劫.apply(丑))
    assertSame(丑, fun地劫.apply(寅))
    assertSame(寅, fun地劫.apply(卯))
    assertSame(卯, fun地劫.apply(辰))
    assertSame(辰, fun地劫.apply(巳))
    assertSame(巳, fun地劫.apply(午))
    assertSame(午, fun地劫.apply(未))
    assertSame(未, fun地劫.apply(申))
    assertSame(申, fun地劫.apply(酉))
    assertSame(酉, fun地劫.apply(戌))
    assertSame(戌, fun地劫.apply(亥))
  }

  @Test
  fun test地空() {
    assertSame(亥, fun地空.apply(子))
    assertSame(戌, fun地空.apply(丑))
    assertSame(酉, fun地空.apply(寅))
    assertSame(申, fun地空.apply(卯))
    assertSame(未, fun地空.apply(辰))
    assertSame(午, fun地空.apply(巳))
    assertSame(巳, fun地空.apply(午))
    assertSame(辰, fun地空.apply(未))
    assertSame(卯, fun地空.apply(申))
    assertSame(寅, fun地空.apply(酉))
    assertSame(丑, fun地空.apply(戌))
    assertSame(子, fun地空.apply(亥))
  }
}