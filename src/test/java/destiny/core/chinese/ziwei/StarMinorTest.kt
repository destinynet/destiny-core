/**
 * Created by smallufo on 2017-04-12.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch.*
import destiny.core.chinese.Stem.丁
import destiny.core.chinese.StemBranch.己酉
import destiny.core.chinese.ziwei.StarMinor.Companion.fun三台_月支
import destiny.core.chinese.ziwei.StarMinor.Companion.fun三台_月數
import destiny.core.chinese.ziwei.StarMinor.Companion.fun八座_月支
import destiny.core.chinese.ziwei.StarMinor.Companion.fun八座_月數
import destiny.core.chinese.ziwei.StarMinor.Companion.fun天刑_月支
import destiny.core.chinese.ziwei.StarMinor.Companion.fun天刑_月數
import destiny.core.chinese.ziwei.StarMinor.Companion.fun天哭
import destiny.core.chinese.ziwei.StarMinor.Companion.fun天喜
import destiny.core.chinese.ziwei.StarMinor.Companion.fun天壽
import destiny.core.chinese.ziwei.StarMinor.Companion.fun天姚_月支
import destiny.core.chinese.ziwei.StarMinor.Companion.fun天姚_月數
import destiny.core.chinese.ziwei.StarMinor.Companion.fun天巫_月支
import destiny.core.chinese.ziwei.StarMinor.Companion.fun天巫_月數
import destiny.core.chinese.ziwei.StarMinor.Companion.fun天德
import destiny.core.chinese.ziwei.StarMinor.Companion.fun天才
import destiny.core.chinese.ziwei.StarMinor.Companion.fun天月_月支
import destiny.core.chinese.ziwei.StarMinor.Companion.fun天月_月數
import destiny.core.chinese.ziwei.StarMinor.Companion.fun天貴
import destiny.core.chinese.ziwei.StarMinor.Companion.fun恩光
import destiny.core.chinese.ziwei.StarMinor.Companion.fun月德
import destiny.core.chinese.ziwei.StarMinor.Companion.fun紅鸞
import destiny.core.chinese.ziwei.StarMinor.Companion.fun解神_月支
import destiny.core.chinese.ziwei.StarMinor.Companion.fun解神_月數
import destiny.core.chinese.ziwei.StarMinor.Companion.fun陰煞_月支
import destiny.core.chinese.ziwei.StarMinor.Companion.fun陰煞_月數
import destiny.core.chinese.ziwei.StarMinor.Companion.fun鳳閣
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertSame

class StarMinorTest {

  private val logger = LoggerFactory.getLogger(javaClass)

  @Test
  fun testToString() {
    for (star in StarMinor.values) {
      assertNotNull(star.toString())
      assertNotNull(star.toString(Locale.TAIWAN))
      assertNotNull(star.toString(Locale.CHINA))
      logger.info("tw = {}({}) , cn = {}({})",
        star.toString(Locale.TAIWAN), star.getAbbreviation(Locale.TAIWAN),
        star.toString(Locale.CHINA), star.getAbbreviation(Locale.CHINA))
    }
  }

  @Test
  fun test天刑() {
    assertSame(酉, fun天刑_月支.invoke(寅))
    assertSame(戌, fun天刑_月支.invoke(卯))
    assertSame(亥, fun天刑_月支.invoke(辰))
    assertSame(子, fun天刑_月支.invoke(巳))
    assertSame(丑, fun天刑_月支.invoke(午))
    assertSame(寅, fun天刑_月支.invoke(未))
    assertSame(卯, fun天刑_月支.invoke(申))
    assertSame(辰, fun天刑_月支.invoke(酉))
    assertSame(巳, fun天刑_月支.invoke(戌))
    assertSame(午, fun天刑_月支.invoke(亥))
    assertSame(未, fun天刑_月支.invoke(子))
    assertSame(申, fun天刑_月支.invoke(丑))

    assertSame(酉, fun天刑_月數.invoke(1))
    assertSame(戌, fun天刑_月數.invoke(2))
    assertSame(亥, fun天刑_月數.invoke(3))
    assertSame(子, fun天刑_月數.invoke(4))
    assertSame(丑, fun天刑_月數.invoke(5))
    assertSame(寅, fun天刑_月數.invoke(6))
    assertSame(卯, fun天刑_月數.invoke(7))
    assertSame(辰, fun天刑_月數.invoke(8))
    assertSame(巳, fun天刑_月數.invoke(9))
    assertSame(午, fun天刑_月數.invoke(10))
    assertSame(未, fun天刑_月數.invoke(11))
    assertSame(申, fun天刑_月數.invoke(12))
  }


  @Test
  fun test天姚() {
    assertSame(丑, fun天姚_月支.invoke(寅))
    assertSame(寅, fun天姚_月支.invoke(卯))
    assertSame(卯, fun天姚_月支.invoke(辰))
    assertSame(辰, fun天姚_月支.invoke(巳))
    assertSame(巳, fun天姚_月支.invoke(午))
    assertSame(午, fun天姚_月支.invoke(未))
    assertSame(未, fun天姚_月支.invoke(申))
    assertSame(申, fun天姚_月支.invoke(酉))
    assertSame(酉, fun天姚_月支.invoke(戌))
    assertSame(戌, fun天姚_月支.invoke(亥))
    assertSame(亥, fun天姚_月支.invoke(子))
    assertSame(子, fun天姚_月支.invoke(丑))

    assertSame(丑, fun天姚_月數.invoke(1))
    assertSame(寅, fun天姚_月數.invoke(2))
    assertSame(卯, fun天姚_月數.invoke(3))
    assertSame(辰, fun天姚_月數.invoke(4))
    assertSame(巳, fun天姚_月數.invoke(5))
    assertSame(午, fun天姚_月數.invoke(6))
    assertSame(未, fun天姚_月數.invoke(7))
    assertSame(申, fun天姚_月數.invoke(8))
    assertSame(酉, fun天姚_月數.invoke(9))
    assertSame(戌, fun天姚_月數.invoke(10))
    assertSame(亥, fun天姚_月數.invoke(11))
    assertSame(子, fun天姚_月數.invoke(12))
  }


  @Test
  fun test解神() {
    assertSame(申, fun解神_月支.invoke(寅))
    assertSame(申, fun解神_月支.invoke(卯))
    assertSame(戌, fun解神_月支.invoke(辰))
    assertSame(戌, fun解神_月支.invoke(巳))
    assertSame(子, fun解神_月支.invoke(午))
    assertSame(子, fun解神_月支.invoke(未))
    assertSame(寅, fun解神_月支.invoke(申))
    assertSame(寅, fun解神_月支.invoke(酉))
    assertSame(辰, fun解神_月支.invoke(戌))
    assertSame(辰, fun解神_月支.invoke(亥))
    assertSame(午, fun解神_月支.invoke(子))
    assertSame(午, fun解神_月支.invoke(丑))

    assertSame(申, fun解神_月數.invoke(1))
    assertSame(申, fun解神_月數.invoke(2))
    assertSame(戌, fun解神_月數.invoke(3))
    assertSame(戌, fun解神_月數.invoke(4))
    assertSame(子, fun解神_月數.invoke(5))
    assertSame(子, fun解神_月數.invoke(6))
    assertSame(寅, fun解神_月數.invoke(7))
    assertSame(寅, fun解神_月數.invoke(8))
    assertSame(辰, fun解神_月數.invoke(9))
    assertSame(辰, fun解神_月數.invoke(10))
    assertSame(午, fun解神_月數.invoke(11))
    assertSame(午, fun解神_月數.invoke(12))
  }


  @Test
  fun test天巫() {
    assertSame(巳, fun天巫_月支.invoke(寅))
    assertSame(申, fun天巫_月支.invoke(卯))
    assertSame(寅, fun天巫_月支.invoke(辰))
    assertSame(亥, fun天巫_月支.invoke(巳))
    assertSame(巳, fun天巫_月支.invoke(午))
    assertSame(申, fun天巫_月支.invoke(未))
    assertSame(寅, fun天巫_月支.invoke(申))
    assertSame(亥, fun天巫_月支.invoke(酉))
    assertSame(巳, fun天巫_月支.invoke(戌))
    assertSame(申, fun天巫_月支.invoke(亥))
    assertSame(寅, fun天巫_月支.invoke(子))
    assertSame(亥, fun天巫_月支.invoke(丑))

    assertSame(巳, fun天巫_月數.invoke(1))
    assertSame(申, fun天巫_月數.invoke(2))
    assertSame(寅, fun天巫_月數.invoke(3))
    assertSame(亥, fun天巫_月數.invoke(4))
    assertSame(巳, fun天巫_月數.invoke(5))
    assertSame(申, fun天巫_月數.invoke(6))
    assertSame(寅, fun天巫_月數.invoke(7))
    assertSame(亥, fun天巫_月數.invoke(8))
    assertSame(巳, fun天巫_月數.invoke(9))
    assertSame(申, fun天巫_月數.invoke(10))
    assertSame(寅, fun天巫_月數.invoke(11))
    assertSame(亥, fun天巫_月數.invoke(12))
  }


  @Test
  fun test天月() {
    assertSame(戌, fun天月_月支.invoke(寅))
    assertSame(巳, fun天月_月支.invoke(卯))
    assertSame(辰, fun天月_月支.invoke(辰))
    assertSame(寅, fun天月_月支.invoke(巳))
    assertSame(未, fun天月_月支.invoke(午))
    assertSame(卯, fun天月_月支.invoke(未))
    assertSame(亥, fun天月_月支.invoke(申))
    assertSame(未, fun天月_月支.invoke(酉))
    assertSame(寅, fun天月_月支.invoke(戌))
    assertSame(午, fun天月_月支.invoke(亥))
    assertSame(戌, fun天月_月支.invoke(子))
    assertSame(午, fun天月_月支.invoke(丑))

    assertSame(戌, fun天月_月數.invoke(1))
    assertSame(巳, fun天月_月數.invoke(2))
    assertSame(辰, fun天月_月數.invoke(3))
    assertSame(寅, fun天月_月數.invoke(4))
    assertSame(未, fun天月_月數.invoke(5))
    assertSame(卯, fun天月_月數.invoke(6))
    assertSame(亥, fun天月_月數.invoke(7))
    assertSame(未, fun天月_月數.invoke(8))
    assertSame(寅, fun天月_月數.invoke(9))
    assertSame(午, fun天月_月數.invoke(10))
    assertSame(戌, fun天月_月數.invoke(11))
    assertSame(午, fun天月_月數.invoke(12))
  }

  @Test
  fun test陰煞() {
    assertSame(寅, fun陰煞_月支.invoke(寅))
    assertSame(子, fun陰煞_月支.invoke(卯))
    assertSame(戌, fun陰煞_月支.invoke(辰))
    assertSame(申, fun陰煞_月支.invoke(巳))
    assertSame(午, fun陰煞_月支.invoke(午))
    assertSame(辰, fun陰煞_月支.invoke(未))
    assertSame(寅, fun陰煞_月支.invoke(申))
    assertSame(子, fun陰煞_月支.invoke(酉))
    assertSame(戌, fun陰煞_月支.invoke(戌))
    assertSame(申, fun陰煞_月支.invoke(亥))
    assertSame(午, fun陰煞_月支.invoke(子))
    assertSame(辰, fun陰煞_月支.invoke(丑))

    assertSame(寅, fun陰煞_月數.invoke(1))
    assertSame(子, fun陰煞_月數.invoke(2))
    assertSame(戌, fun陰煞_月數.invoke(3))
    assertSame(申, fun陰煞_月數.invoke(4))
    assertSame(午, fun陰煞_月數.invoke(5))
    assertSame(辰, fun陰煞_月數.invoke(6))
    assertSame(寅, fun陰煞_月數.invoke(7))
    assertSame(子, fun陰煞_月數.invoke(8))
    assertSame(戌, fun陰煞_月數.invoke(9))
    assertSame(申, fun陰煞_月數.invoke(10))
    assertSame(午, fun陰煞_月數.invoke(11))
    assertSame(辰, fun陰煞_月數.invoke(12))
  }


  @Test
  fun test天哭() {
    assertSame(午, fun天哭.invoke(子))
    assertSame(巳, fun天哭.invoke(丑))
    assertSame(辰, fun天哭.invoke(寅))
    assertSame(卯, fun天哭.invoke(卯))
    assertSame(寅, fun天哭.invoke(辰))
    assertSame(丑, fun天哭.invoke(巳))
    assertSame(子, fun天哭.invoke(午))
    assertSame(亥, fun天哭.invoke(未))
    assertSame(戌, fun天哭.invoke(申))
    assertSame(酉, fun天哭.invoke(酉))
    assertSame(申, fun天哭.invoke(戌))
    assertSame(未, fun天哭.invoke(亥))
  }

  @Test
  fun test鳳閣() {
    assertSame(戌, fun鳳閣.invoke(子))
    assertSame(酉, fun鳳閣.invoke(丑))
    assertSame(申, fun鳳閣.invoke(寅))
    assertSame(未, fun鳳閣.invoke(卯))
    assertSame(午, fun鳳閣.invoke(辰))
    assertSame(巳, fun鳳閣.invoke(巳))
    assertSame(辰, fun鳳閣.invoke(午))
    assertSame(卯, fun鳳閣.invoke(未))
    assertSame(寅, fun鳳閣.invoke(申))
    assertSame(丑, fun鳳閣.invoke(酉))
    assertSame(子, fun鳳閣.invoke(戌))
    assertSame(亥, fun鳳閣.invoke(亥))
  }

  @Test
  fun test紅鸞() {
    assertSame(卯, fun紅鸞.invoke(子))
    assertSame(寅, fun紅鸞.invoke(丑))
    assertSame(丑, fun紅鸞.invoke(寅))
    assertSame(子, fun紅鸞.invoke(卯))
    assertSame(亥, fun紅鸞.invoke(辰))
    assertSame(戌, fun紅鸞.invoke(巳))
    assertSame(酉, fun紅鸞.invoke(午))
    assertSame(申, fun紅鸞.invoke(未))
    assertSame(未, fun紅鸞.invoke(申))
    assertSame(午, fun紅鸞.invoke(酉))
    assertSame(巳, fun紅鸞.invoke(戌))
    assertSame(辰, fun紅鸞.invoke(亥))
  }

  @Test
  fun test天喜() {
    assertSame(酉, fun天喜.invoke(子))
    assertSame(申, fun天喜.invoke(丑))
    assertSame(未, fun天喜.invoke(寅))
    assertSame(午, fun天喜.invoke(卯))
    assertSame(巳, fun天喜.invoke(辰))
    assertSame(辰, fun天喜.invoke(巳))
    assertSame(卯, fun天喜.invoke(午))
    assertSame(寅, fun天喜.invoke(未))
    assertSame(丑, fun天喜.invoke(申))
    assertSame(子, fun天喜.invoke(酉))
    assertSame(亥, fun天喜.invoke(戌))
    assertSame(戌, fun天喜.invoke(亥))
  }

  @Test
  fun test天德() {
    assertSame(酉, fun天德.invoke(子))
    assertSame(戌, fun天德.invoke(丑))
    assertSame(亥, fun天德.invoke(寅))
    assertSame(子, fun天德.invoke(卯))
    assertSame(丑, fun天德.invoke(辰))
    assertSame(寅, fun天德.invoke(巳))
    assertSame(卯, fun天德.invoke(午))
    assertSame(辰, fun天德.invoke(未))
    assertSame(巳, fun天德.invoke(申))
    assertSame(午, fun天德.invoke(酉))
    assertSame(未, fun天德.invoke(戌))
    assertSame(申, fun天德.invoke(亥))
  }

  @Test
  fun test月德() {
    assertSame(巳, fun月德.invoke(子))
    assertSame(午, fun月德.invoke(丑))
    assertSame(未, fun月德.invoke(寅))
    assertSame(申, fun月德.invoke(卯))
    assertSame(酉, fun月德.invoke(辰))
    assertSame(戌, fun月德.invoke(巳))
    assertSame(亥, fun月德.invoke(午))
    assertSame(子, fun月德.invoke(未))
    assertSame(丑, fun月德.invoke(申))
    assertSame(寅, fun月德.invoke(酉))
    assertSame(卯, fun月德.invoke(戌))
    assertSame(辰, fun月德.invoke(亥))
  }

  @Test
  fun test日系星() {
    // 2017-04-12 丁酉年 三月十六日 巳時,
    // 三台在 (己)酉
    assertSame(酉, fun三台_月支.invoke(辰, 16))
    assertSame(酉, fun三台_月數.invoke(3, 16))
    // 八座在 (乙)巳
    assertSame(巳, fun八座_月支.invoke(辰, 16))
    assertSame(巳, fun八座_月數.invoke(3, 16))

    // 恩光在 (丁)未
    assertSame(未, fun恩光.invoke(16, 巳))
    // 天貴在 (辛)亥
    assertSame(亥, fun天貴.invoke(16, 巳))
  }

  @Test
  fun test天才天壽() {
    // 2017-04-12 丁酉年 三月十六日 巳時

    // 命宮 在 (辛)亥
    assertSame(亥, IZiwei.getMainHouseBranch(3, 巳))
    // 天才 在 (戊)申
    assertSame(申, fun天才.invoke(酉, 3, 巳))

    // 身宮 在 (己)酉
    assertSame(酉, IZiwei.getBodyHouseBranch(3, 巳))
    assertSame(己酉, IZiwei.getBodyHouse(丁, 3, 巳))
    // 天壽 在 (丙)午
    assertSame(午, fun天壽.invoke(酉, 3, 巳))

  }


}