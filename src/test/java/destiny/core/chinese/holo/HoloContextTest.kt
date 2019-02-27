/**
 * Created by smallufo on 2019-02-28.
 */
package destiny.core.chinese.holo

import destiny.core.Gender.*
import destiny.core.calendar.eightwords.EightWords
import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.*
import destiny.core.chinese.StemBranch
import destiny.core.chinese.YuanGenderImpl
import destiny.fengshui.sanyuan.Yuan.*
import destiny.iching.Hexagram
import destiny.iching.Symbol.*
import org.junit.Assert.*
import kotlin.test.Test

class HoloContextTest {

  val context = HoloContext(NumberizeImpl(), YuanGenderImpl())

  @Test
  fun case1() {
    val ew = EightWords(StemBranch.甲子, StemBranch.丁卯, StemBranch.庚申, StemBranch.庚辰)

    // 並非餘5 , 所以，上中下元 無關 , 男女 陰陽 都無關
    // 31 -> 6 -> 乾
    assertEquals(乾, context.getYangSymbol(ew, 男, UP, true))
    assertEquals(乾, context.getYangSymbol(ew, 男, MID, true))
    assertEquals(乾, context.getYangSymbol(ew, 男, LOW, true))
    assertEquals(乾, context.getYangSymbol(ew, 女, UP, true))
    assertEquals(乾, context.getYangSymbol(ew, 女, MID, true))
    assertEquals(乾, context.getYangSymbol(ew, 女, LOW, true))

    // 34 -> 4 -> 巽
    assertEquals(巽, context.getYinSymbol(ew, 男, UP, true))
    assertEquals(巽, context.getYinSymbol(ew, 男, MID, true))
    assertEquals(巽, context.getYinSymbol(ew, 男, LOW, true))
    assertEquals(巽, context.getYinSymbol(ew, 女, UP, true))
    assertEquals(巽, context.getYinSymbol(ew, 女, MID, true))
    assertEquals(巽, context.getYinSymbol(ew, 女, LOW, true))

    // 陽男
    assertEquals(Hexagram.姤, context.getHexagram(ew, 男, UP, true))
    assertEquals(Hexagram.姤, context.getHexagram(ew, 男, MID, true))
    assertEquals(Hexagram.姤, context.getHexagram(ew, 男, LOW, true))
  }

  @Test
  fun case2() {
    val ew = EightWords(StemBranch.丁巳, StemBranch.丙午, StemBranch.壬寅, StemBranch.辛丑)

    // 29 -> 4 -> 巽
    assertEquals(巽, context.getYangSymbol(ew, 男, UP, true))
    assertEquals(巽, context.getYangSymbol(ew, 男, MID, true))
    assertEquals(巽, context.getYangSymbol(ew, 男, LOW, true))
    assertEquals(巽, context.getYangSymbol(ew, 女, UP, true))
    assertEquals(巽, context.getYangSymbol(ew, 女, MID, true))
    assertEquals(巽, context.getYangSymbol(ew, 女, LOW, true))

    // 40 -> 10 -> 坎
    assertEquals(坎, context.getYinSymbol(ew, 男, UP, true))
    assertEquals(坎, context.getYinSymbol(ew, 男, MID, true))
    assertEquals(坎, context.getYinSymbol(ew, 男, LOW, true))
    assertEquals(坎, context.getYinSymbol(ew, 女, UP, true))
    assertEquals(坎, context.getYinSymbol(ew, 女, MID, true))
    assertEquals(坎, context.getYinSymbol(ew, 女, LOW, true))

    // 陰男
    assertEquals(Hexagram.井, context.getHexagram(ew, 男, UP, false))
    assertEquals(Hexagram.井, context.getHexagram(ew, 男, MID, false))
    assertEquals(Hexagram.井, context.getHexagram(ew, 男, LOW, false))
  }

  @Test
  fun case3() {
    val ew = EightWords(StemBranch.庚午, StemBranch.丙戌, StemBranch.己酉, StemBranch.乙亥)

    // 34 -> 9 -> 離
    assertEquals(離, context.getYangSymbol(ew, 男, UP, true))
    assertEquals(離, context.getYangSymbol(ew, 男, MID, true))
    assertEquals(離, context.getYangSymbol(ew, 男, LOW, true))
    assertEquals(離, context.getYangSymbol(ew, 女, UP, true))
    assertEquals(離, context.getYangSymbol(ew, 女, MID, true))
    assertEquals(離, context.getYangSymbol(ew, 女, LOW, true))

    // 32 -> 2 -> 坤
    assertEquals(坤, context.getYinSymbol(ew, 男, UP, true))
    assertEquals(坤, context.getYinSymbol(ew, 男, MID, true))
    assertEquals(坤, context.getYinSymbol(ew, 男, LOW, true))
    assertEquals(坤, context.getYinSymbol(ew, 女, UP, true))
    assertEquals(坤, context.getYinSymbol(ew, 女, MID, true))
    assertEquals(坤, context.getYinSymbol(ew, 女, LOW, true))

    // 陽女
    assertEquals(Hexagram.明夷, context.getHexagram(ew, 女, UP, true))
    assertEquals(Hexagram.明夷, context.getHexagram(ew, 女, MID, true))
    assertEquals(Hexagram.明夷, context.getHexagram(ew, 女, LOW, true))
  }

  @Test
  fun test4() {
    val ew = EightWords(StemBranch.癸卯, StemBranch.壬戌, StemBranch.甲申, StemBranch.辛未)

    // 22 -> 22 - 20 = 2 -> 坤
    assertEquals(坤, context.getYangSymbol(ew, 男, UP, true))
    assertEquals(坤, context.getYangSymbol(ew, 男, MID, true))
    assertEquals(坤, context.getYangSymbol(ew, 男, LOW, true))
    assertEquals(坤, context.getYangSymbol(ew, 女, UP, true))
    assertEquals(坤, context.getYangSymbol(ew, 女, MID, true))
    assertEquals(坤, context.getYangSymbol(ew, 女, LOW, true))

    // 50 -> 50 - 30 = 20 -> 2 -> 坤
    assertEquals(坤, context.getYinSymbol(ew, 男, UP, true))
    assertEquals(坤, context.getYinSymbol(ew, 男, MID, true))
    assertEquals(坤, context.getYinSymbol(ew, 男, LOW, true))
    assertEquals(坤, context.getYinSymbol(ew, 女, UP, true))
    assertEquals(坤, context.getYinSymbol(ew, 女, MID, true))
    assertEquals(坤, context.getYinSymbol(ew, 女, LOW, true))

    // 陰女
    assertEquals(Hexagram.坤, context.getHexagram(ew, 女, UP, false))
    assertEquals(Hexagram.坤, context.getHexagram(ew, 女, MID, false))
    assertEquals(Hexagram.坤, context.getHexagram(ew, 女, LOW, false))
  }

  @Test
  fun testYuanTang_Yang() {
    // 一陽
    Hexagram.師.also { hex ->
      assertSame(2, context.getYuanTang(hex, 子))
      assertSame(2, context.getYuanTang(hex, 丑))
      assertSame(1, context.getYuanTang(hex, 寅))
      assertSame(3, context.getYuanTang(hex, 卯))
      assertSame(4, context.getYuanTang(hex, 辰))
      assertSame(5, context.getYuanTang(hex, 巳))
    }


    // 二陽
    Hexagram.萃.also { hex ->
      assertSame(4, context.getYuanTang(hex, 子))
      assertSame(5, context.getYuanTang(hex, 丑))
      assertSame(4, context.getYuanTang(hex, 寅))
      assertSame(5, context.getYuanTang(hex, 卯))
      assertSame(1, context.getYuanTang(hex, 辰))
      assertSame(2, context.getYuanTang(hex, 巳))
    }

    // 三陽
    Hexagram.旅.also { hex ->
      assertSame(3, context.getYuanTang(hex, 子))
      assertSame(4, context.getYuanTang(hex, 丑))
      assertSame(6, context.getYuanTang(hex, 寅))
      assertSame(3, context.getYuanTang(hex, 卯))
      assertSame(4, context.getYuanTang(hex, 辰))
      assertSame(6, context.getYuanTang(hex, 巳))
    }

    // 四陽
    Hexagram.巽.also { hex ->
      assertSame(2, context.getYuanTang(hex, 子))
      assertSame(3, context.getYuanTang(hex, 丑))
      assertSame(5, context.getYuanTang(hex, 寅))
      assertSame(6, context.getYuanTang(hex, 卯))
      assertSame(1, context.getYuanTang(hex, 辰))
      assertSame(4, context.getYuanTang(hex, 巳))
    }

    // 五陽
    Hexagram.同人.also { hex ->
      assertSame(1, context.getYuanTang(hex, 子))
      assertSame(3, context.getYuanTang(hex, 丑))
      assertSame(4, context.getYuanTang(hex, 寅))
      assertSame(5, context.getYuanTang(hex, 卯))
      assertSame(6, context.getYuanTang(hex, 辰))
      assertSame(2, context.getYuanTang(hex, 巳))
    }
  }

  @Test
  fun testYuanTang_Yin() {
    // 一陰
    Hexagram.小畜.also { hex ->
      assertSame(4, context.getYuanTang(hex, 午))
      assertSame(4, context.getYuanTang(hex, 未))
      assertSame(1, context.getYuanTang(hex, 申))
      assertSame(2, context.getYuanTang(hex, 酉))
      assertSame(3, context.getYuanTang(hex, 戌))
      assertSame(5, context.getYuanTang(hex, 亥))
    }

    // 二陰
    Hexagram.無妄.also { hex ->
      assertSame(2, context.getYuanTang(hex, 午))
      assertSame(3, context.getYuanTang(hex, 未))
      assertSame(2, context.getYuanTang(hex, 申))
      assertSame(3, context.getYuanTang(hex, 酉))
      assertSame(1, context.getYuanTang(hex, 戌))
      assertSame(4, context.getYuanTang(hex, 亥))
    }

    // 三陰
    Hexagram.節.also { hex ->
      assertSame(3, context.getYuanTang(hex, 午))
      assertSame(4, context.getYuanTang(hex, 未))
      assertSame(6, context.getYuanTang(hex, 申))
      assertSame(3, context.getYuanTang(hex, 酉))
      assertSame(4, context.getYuanTang(hex, 戌))
      assertSame(6, context.getYuanTang(hex, 亥))
    }

    // 四陰
    Hexagram.震.also { hex ->
      assertSame(2, context.getYuanTang(hex, 午))
      assertSame(3, context.getYuanTang(hex, 未))
      assertSame(5, context.getYuanTang(hex, 申))
      assertSame(6, context.getYuanTang(hex, 酉))
      assertSame(1, context.getYuanTang(hex, 戌))
      assertSame(4, context.getYuanTang(hex, 亥))
    }

    // 五陰
    Hexagram.豫.also { hex ->
      assertSame(1, context.getYuanTang(hex, 午))
      assertSame(2, context.getYuanTang(hex, 未))
      assertSame(3, context.getYuanTang(hex, 申))
      assertSame(5, context.getYuanTang(hex, 酉))
      assertSame(6, context.getYuanTang(hex, 戌))
      assertSame(4, context.getYuanTang(hex, 亥))
    }
  }
}