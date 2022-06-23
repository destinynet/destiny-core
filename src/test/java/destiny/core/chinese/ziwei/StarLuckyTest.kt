/**
 * Created by smallufo on 2017-04-11.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch.*
import destiny.core.chinese.Stem.*
import destiny.core.chinese.impls.TianyiZiweiBookImpl
import destiny.core.getAbbreviation
import destiny.core.toString
import mu.KotlinLogging
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertSame

// TODO : AbstractPointTest : 年馬(nameKey = '年馬') 無法反查字串 , shuffle error
class StarLuckyTest {

  private val logger = KotlinLogging.logger { }

  @Test
  fun testToString() {
    assertEquals("左輔", StarLucky.左輔.toString(Locale.TAIWAN))
    assertEquals("左辅", StarLucky.左輔.toString(Locale.SIMPLIFIED_CHINESE))
    assertEquals("左輔", StarLucky.左輔.toString(Locale.ENGLISH))
  }

  @Test
  fun testAbbr() {
    assertEquals("輔", StarLucky.左輔.getAbbreviation(Locale.TAIWAN))
    assertEquals("辅", StarLucky.左輔.getAbbreviation(Locale.SIMPLIFIED_CHINESE))
  }



  @Test
  fun testToStrings() {
    for (star in StarLucky.values) {
      assertNotNull(star.toString())
      assertNotNull(star.toString(Locale.TAIWAN))
      assertNotNull(star.toString(Locale.CHINA))
      logger.info("tw = {}({}) , cn = {}({})",
                  star.toString(Locale.TAIWAN), star.getAbbreviation(Locale.TAIWAN),
                  star.toString(Locale.CHINA), star.getAbbreviation(Locale.CHINA))
    }
  }

  @Test
  fun test文昌() {
    assertSame(戌, StarLucky.fun文昌.invoke(子))
    assertSame(酉, StarLucky.fun文昌.invoke(丑))
    assertSame(申, StarLucky.fun文昌.invoke(寅))
    assertSame(未, StarLucky.fun文昌.invoke(卯))
    assertSame(午, StarLucky.fun文昌.invoke(辰))
    assertSame(巳, StarLucky.fun文昌.invoke(巳))
    assertSame(辰, StarLucky.fun文昌.invoke(午))
    assertSame(卯, StarLucky.fun文昌.invoke(未))
    assertSame(寅, StarLucky.fun文昌.invoke(申))
    assertSame(丑, StarLucky.fun文昌.invoke(酉))
    assertSame(子, StarLucky.fun文昌.invoke(戌))
    assertSame(亥, StarLucky.fun文昌.invoke(亥))
  }

  @Test
  fun test文曲() {
    assertSame(辰, StarLucky.fun文曲.invoke(子))
    assertSame(巳, StarLucky.fun文曲.invoke(丑))
    assertSame(午, StarLucky.fun文曲.invoke(寅))
    assertSame(未, StarLucky.fun文曲.invoke(卯))
    assertSame(申, StarLucky.fun文曲.invoke(辰))
    assertSame(酉, StarLucky.fun文曲.invoke(巳))
    assertSame(戌, StarLucky.fun文曲.invoke(午))
    assertSame(亥, StarLucky.fun文曲.invoke(未))
    assertSame(子, StarLucky.fun文曲.invoke(申))
    assertSame(丑, StarLucky.fun文曲.invoke(酉))
    assertSame(寅, StarLucky.fun文曲.invoke(戌))
    assertSame(卯, StarLucky.fun文曲.invoke(亥))
  }

  @Test
  fun test左輔() {
    assertSame(辰, StarLucky.fun左輔_月數.invoke(1))
    assertSame(巳, StarLucky.fun左輔_月數.invoke(2))
    assertSame(午, StarLucky.fun左輔_月數.invoke(3))
    assertSame(未, StarLucky.fun左輔_月數.invoke(4))
    assertSame(申, StarLucky.fun左輔_月數.invoke(5))
    assertSame(酉, StarLucky.fun左輔_月數.invoke(6))
    assertSame(戌, StarLucky.fun左輔_月數.invoke(7))
    assertSame(亥, StarLucky.fun左輔_月數.invoke(8))
    assertSame(子, StarLucky.fun左輔_月數.invoke(9))
    assertSame(丑, StarLucky.fun左輔_月數.invoke(10))
    assertSame(寅, StarLucky.fun左輔_月數.invoke(11))
    assertSame(卯, StarLucky.fun左輔_月數.invoke(12))
  }

  @Test
  fun test右弼() {
    assertSame(戌, StarLucky.fun右弼_月數.invoke(1))
    assertSame(酉, StarLucky.fun右弼_月數.invoke(2))
    assertSame(申, StarLucky.fun右弼_月數.invoke(3))
    assertSame(未, StarLucky.fun右弼_月數.invoke(4))
    assertSame(午, StarLucky.fun右弼_月數.invoke(5))
    assertSame(巳, StarLucky.fun右弼_月數.invoke(6))
    assertSame(辰, StarLucky.fun右弼_月數.invoke(7))
    assertSame(卯, StarLucky.fun右弼_月數.invoke(8))
    assertSame(寅, StarLucky.fun右弼_月數.invoke(9))
    assertSame(丑, StarLucky.fun右弼_月數.invoke(10))
    assertSame(子, StarLucky.fun右弼_月數.invoke(11))
    assertSame(亥, StarLucky.fun右弼_月數.invoke(12))
  }

  @Test
  fun test天魁() {
    val tianyiImpl = TianyiZiweiBookImpl()
    assertSame(丑, StarLucky.fun天魁.invoke(甲, tianyiImpl))
    assertSame(子, StarLucky.fun天魁.invoke(乙, tianyiImpl))
    assertSame(亥, StarLucky.fun天魁.invoke(丙, tianyiImpl))
    assertSame(亥, StarLucky.fun天魁.invoke(丁, tianyiImpl))
    assertSame(丑, StarLucky.fun天魁.invoke(戊, tianyiImpl))
    assertSame(子, StarLucky.fun天魁.invoke(己, tianyiImpl))
    assertSame(丑, StarLucky.fun天魁.invoke(庚, tianyiImpl))
    assertSame(午, StarLucky.fun天魁.invoke(辛, tianyiImpl))
    assertSame(卯, StarLucky.fun天魁.invoke(壬, tianyiImpl))
    assertSame(卯, StarLucky.fun天魁.invoke(癸, tianyiImpl))
  }

  @Test
  fun test天鉞() {
    val tianyiImpl = TianyiZiweiBookImpl()
    assertSame(未, StarLucky.fun天鉞.invoke(甲, tianyiImpl))
    assertSame(申, StarLucky.fun天鉞.invoke(乙, tianyiImpl))
    assertSame(酉, StarLucky.fun天鉞.invoke(丙, tianyiImpl))
    assertSame(酉, StarLucky.fun天鉞.invoke(丁, tianyiImpl))
    assertSame(未, StarLucky.fun天鉞.invoke(戊, tianyiImpl))
    assertSame(申, StarLucky.fun天鉞.invoke(己, tianyiImpl))
    assertSame(未, StarLucky.fun天鉞.invoke(庚, tianyiImpl))
    assertSame(寅, StarLucky.fun天鉞.invoke(辛, tianyiImpl))
    assertSame(巳, StarLucky.fun天鉞.invoke(壬, tianyiImpl))
    assertSame(巳, StarLucky.fun天鉞.invoke(癸, tianyiImpl))
  }

  @Test
  fun test祿存() {
    assertSame(寅, StarLucky.fun祿存.invoke(甲))
    assertSame(卯, StarLucky.fun祿存.invoke(乙))
    assertSame(巳, StarLucky.fun祿存.invoke(丙))
    assertSame(午, StarLucky.fun祿存.invoke(丁))
    assertSame(巳, StarLucky.fun祿存.invoke(戊))
    assertSame(午, StarLucky.fun祿存.invoke(己))
    assertSame(申, StarLucky.fun祿存.invoke(庚))
    assertSame(酉, StarLucky.fun祿存.invoke(辛))
    assertSame(亥, StarLucky.fun祿存.invoke(壬))
    assertSame(子, StarLucky.fun祿存.invoke(癸))
  }

  @Test
  fun test天馬() {
    assertSame(寅, StarLucky.fun年馬.invoke(子))
    assertSame(亥, StarLucky.fun年馬.invoke(丑))
    assertSame(申, StarLucky.fun年馬.invoke(寅))
    assertSame(巳, StarLucky.fun年馬.invoke(卯))
    assertSame(寅, StarLucky.fun年馬.invoke(辰))
    assertSame(亥, StarLucky.fun年馬.invoke(巳))
    assertSame(申, StarLucky.fun年馬.invoke(午))
    assertSame(巳, StarLucky.fun年馬.invoke(未))
    assertSame(寅, StarLucky.fun年馬.invoke(申))
    assertSame(亥, StarLucky.fun年馬.invoke(酉))
    assertSame(申, StarLucky.fun年馬.invoke(戌))
    assertSame(巳, StarLucky.fun年馬.invoke(亥))

    assertSame(寅, StarLucky.fun月馬_月數.invoke(11))
    assertSame(亥, StarLucky.fun月馬_月數.invoke(12))
    assertSame(申, StarLucky.fun月馬_月數.invoke(1))
    assertSame(巳, StarLucky.fun月馬_月數.invoke(2))
    assertSame(寅, StarLucky.fun月馬_月數.invoke(3))
    assertSame(亥, StarLucky.fun月馬_月數.invoke(4))
    assertSame(申, StarLucky.fun月馬_月數.invoke(5))
    assertSame(巳, StarLucky.fun月馬_月數.invoke(6))
    assertSame(寅, StarLucky.fun月馬_月數.invoke(7))
    assertSame(亥, StarLucky.fun月馬_月數.invoke(8))
    assertSame(申, StarLucky.fun月馬_月數.invoke(9))
    assertSame(巳, StarLucky.fun月馬_月數.invoke(10))
  }

}
