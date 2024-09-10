/**
 * Created by smallufo on 2019-04-26.
 */
package destiny.core.iching

import destiny.core.iching.Congenital.Circle.aheadOf
import destiny.core.iching.Congenital.Circle.behindOf
import destiny.core.iching.Congenital.Circle.next
import destiny.core.iching.Congenital.Circle.prev
import destiny.core.iching.Congenital.Table.tableNext
import destiny.core.iching.Congenital.Table.tablePrev
import destiny.core.iching.Congenital.toHexagram
import destiny.core.iching.Congenital.toInt
import destiny.tools.KotlinLogging
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame


class congenitalTest {

  val logger = KotlinLogging.logger {  }

  @Test
  fun testNextSteps() {
    (0..63).forEach { steps ->
      assertEquals(steps , Hexagram.乾.next(steps).aheadOf(Hexagram.乾))
      assertEquals(steps , Hexagram.姤.next(steps).aheadOf(Hexagram.姤))
      assertEquals(steps , Hexagram.坤.next(steps).aheadOf(Hexagram.坤))
      assertEquals(steps , Hexagram.復.next(steps).aheadOf(Hexagram.復))
    }
  }

  @Test
  fun testPrevSteps() {
    (0..63).forEach { steps ->
      assertEquals(steps , Hexagram.乾.prev(steps).behindOf(Hexagram.乾))
      assertEquals(steps , Hexagram.姤.prev(steps).behindOf(Hexagram.姤))
      assertEquals(steps , Hexagram.坤.prev(steps).behindOf(Hexagram.坤))
      assertEquals(steps , Hexagram.復.prev(steps).behindOf(Hexagram.復))
    }
  }

  /**
   * 領先幾步
   */
  @Test
  fun testAheadOf() {
    // 對比「復」
    assertEquals(0 , Hexagram.復.aheadOf(Hexagram.復))
    assertEquals(1 , Hexagram.頤.aheadOf(Hexagram.復))
    assertEquals(31 , Hexagram.乾.aheadOf(Hexagram.復))
    assertEquals(32 , Hexagram.姤.aheadOf(Hexagram.復))
    assertEquals(63 , Hexagram.坤.aheadOf(Hexagram.復))

    // 對比「乾」
    assertEquals(0 , Hexagram.乾.aheadOf(Hexagram.乾))
    assertEquals(1 , Hexagram.姤.aheadOf(Hexagram.乾))
    assertEquals(32 , Hexagram.坤.aheadOf(Hexagram.乾))
    assertEquals(33 , Hexagram.復.aheadOf(Hexagram.乾))
    assertEquals(63 , Hexagram.夬.aheadOf(Hexagram.乾))

    // 對比「姤」
    assertEquals(0 , Hexagram.姤.aheadOf(Hexagram.姤))
    assertEquals(1 , Hexagram.大過.aheadOf(Hexagram.姤))
    assertEquals(31 , Hexagram.坤.aheadOf(Hexagram.姤))
    assertEquals(32 , Hexagram.復.aheadOf(Hexagram.姤))
    assertEquals(63 , Hexagram.乾.aheadOf(Hexagram.姤))

    // 對比「坤」
    assertEquals(0 , Hexagram.坤.aheadOf(Hexagram.坤))
    assertEquals(1 , Hexagram.復.aheadOf(Hexagram.坤))
    assertEquals(32 , Hexagram.乾.aheadOf(Hexagram.坤))
    assertEquals(33 , Hexagram.姤.aheadOf(Hexagram.坤))
    assertEquals(63 , Hexagram.剝.aheadOf(Hexagram.坤))
  }

  /**
   * 落後幾步
   */
  @Test
  fun testBehindOf() {
    // 從「復」開始
    assertEquals(0 , Hexagram.復.behindOf(Hexagram.復))
    assertEquals(1 , Hexagram.復.behindOf(Hexagram.頤))
    assertEquals(31 , Hexagram.復.behindOf(Hexagram.乾))
    assertEquals(32 , Hexagram.復.behindOf(Hexagram.姤))
    assertEquals(63 , Hexagram.復.behindOf(Hexagram.坤))

    // 從「乾」開始
    assertEquals(0 , Hexagram.乾.behindOf(Hexagram.乾))
    assertEquals(1 , Hexagram.乾.behindOf(Hexagram.姤))
    assertEquals(32 , Hexagram.乾.behindOf(Hexagram.坤))
    assertEquals(33 , Hexagram.乾.behindOf(Hexagram.復))
    assertEquals(63 , Hexagram.乾.behindOf(Hexagram.夬))

    // 從「姤」開始
    assertEquals(0 , Hexagram.姤.behindOf(Hexagram.姤))
    assertEquals(1 , Hexagram.姤.behindOf(Hexagram.大過))
    assertEquals(31 , Hexagram.姤.behindOf(Hexagram.坤))
    assertEquals(32 , Hexagram.姤.behindOf(Hexagram.復))
    assertEquals(63 , Hexagram.姤.behindOf(Hexagram.乾))

    // 從「坤」開始
    assertEquals(0 , Hexagram.坤.behindOf(Hexagram.坤))
    assertEquals(1 , Hexagram.坤.behindOf(Hexagram.復))
    assertEquals(32 , Hexagram.坤.behindOf(Hexagram.乾))
    assertEquals(33 , Hexagram.坤.behindOf(Hexagram.姤))
    assertEquals(63 , Hexagram.坤.behindOf(Hexagram.剝))
  }

  @Test
  fun testTimeSeq_順推() {
    generateSequence(Hexagram.乾) {
      it.next()
    }.take(64)
      .forEach {
        logger.info("{}" , it)
      }
  }

  @Test
  fun testTimeSeq_逆推() {
    generateSequence(Hexagram.乾) {
      it.prev()
    }.take(64)
      .forEach {
        logger.info("{}" , it)
      }
  }

  @Test
  fun testIntToHexagram() {
    assertSame(Hexagram.坤 , 0.toHexagram())
    assertSame(Hexagram.剝 , 1.toHexagram())
    assertSame(Hexagram.比 , 2.toHexagram())
    assertSame(Hexagram.姤 , 31.toHexagram())

    assertSame(Hexagram.復 , 32.toHexagram())
    assertSame(Hexagram.頤 , 33.toHexagram())

    assertSame(Hexagram.夬 , 62.toHexagram())
    assertSame(Hexagram.乾 , 63.toHexagram())

    assertSame(Hexagram.坤 , 64.toHexagram())
  }

  @Test
  fun testHexagramToInt() {
    assertEquals(0 , Hexagram.坤.toInt())
    assertEquals(1 , Hexagram.剝.toInt())
    assertEquals(62 , Hexagram.夬.toInt())
    assertEquals(63 , Hexagram.乾.toInt())
  }


  @Test
  fun printAll() {
    generateSequence(Hexagram.乾 to 1) {
      it.first.tableNext() to it.second+1
    }.take(64)
      .forEach {
        logger.info("{}" , it)
      }
  }


  @Test
  fun next() {
    assertSame(Hexagram.夬 , Hexagram.乾.tableNext())
    assertSame(Hexagram.大有 , Hexagram.夬.tableNext())
    assertSame(Hexagram.履 , Hexagram.泰.tableNext())
    assertSame(Hexagram.坤 , Hexagram.剝.tableNext())
    assertSame(Hexagram.乾 , Hexagram.坤.tableNext())
  }

  @Test
  fun prev() {
    assertSame(Hexagram.乾 , Hexagram.夬.tablePrev())
    assertSame(Hexagram.夬 , Hexagram.大有.tablePrev())
    assertSame(Hexagram.泰 , Hexagram.履.tablePrev())
    assertSame(Hexagram.剝 , Hexagram.坤.tablePrev())
    assertSame(Hexagram.坤 , Hexagram.乾.tablePrev())
  }

  /**
   * 順推
   */
  @Test
  fun tableNext_Positive() {
    // ============== 乾 ==============
    assertSame(Hexagram.乾 , Hexagram.乾.tableNext(0))
    assertSame(Hexagram.夬 , Hexagram.乾.tableNext(1))
    assertSame(Hexagram.大有 , Hexagram.乾.tableNext(2))
    assertSame(Hexagram.大壯 , Hexagram.乾.tableNext(3))
    assertSame(Hexagram.小畜 , Hexagram.乾.tableNext(4))
    assertSame(Hexagram.需 , Hexagram.乾.tableNext(5))
    assertSame(Hexagram.大畜 , Hexagram.乾.tableNext(6))
    assertSame(Hexagram.泰 , Hexagram.乾.tableNext(7))

    assertSame(Hexagram.履 , Hexagram.乾.tableNext(8))
    assertSame(Hexagram.臨 , Hexagram.乾.tableNext(15))

    assertSame(Hexagram.同人 , Hexagram.乾.tableNext(16))
    assertSame(Hexagram.無妄 , Hexagram.乾.tableNext(24))
    assertSame(Hexagram.姤 , Hexagram.乾.tableNext(32))
    assertSame(Hexagram.訟 , Hexagram.乾.tableNext(40))
    assertSame(Hexagram.遯 , Hexagram.乾.tableNext(48))

    assertSame(Hexagram.否 , Hexagram.乾.tableNext(56))
    assertSame(Hexagram.坤 , Hexagram.乾.tableNext(63))

    // ============== 大壯 ==============

    assertSame(Hexagram.小畜 , Hexagram.大壯.tableNext(1))
    assertSame(Hexagram.泰 , Hexagram.大壯.tableNext(4))
    assertSame(Hexagram.履 , Hexagram.大壯.tableNext(5))
    assertSame(Hexagram.臨 , Hexagram.大壯.tableNext(12))

    // ============== 震 ==============

    assertSame(Hexagram.益 , Hexagram.震.tableNext(1))
    assertSame(Hexagram.復 , Hexagram.震.tableNext(4))
    assertSame(Hexagram.姤 , Hexagram.震.tableNext(5))
    assertSame(Hexagram.巽 , Hexagram.震.tableNext(9))

    // ============== 坤 ==============

    assertSame(Hexagram.乾 , Hexagram.坤.tableNext(1))
    assertSame(Hexagram.夬 , Hexagram.坤.tableNext(2))
    assertSame(Hexagram.泰 , Hexagram.坤.tableNext(8))

    assertSame(Hexagram.履 , Hexagram.坤.tableNext(9))
    assertSame(Hexagram.臨 , Hexagram.坤.tableNext(16))

    assertSame(Hexagram.同人 , Hexagram.坤.tableNext(17))
    assertSame(Hexagram.明夷 , Hexagram.坤.tableNext(24))

    assertSame(Hexagram.無妄 , Hexagram.坤.tableNext(25))
    assertSame(Hexagram.復 , Hexagram.坤.tableNext(32))

    assertSame(Hexagram.姤 , Hexagram.坤.tableNext(33))
    assertSame(Hexagram.升 , Hexagram.坤.tableNext(40))

    assertSame(Hexagram.訟 , Hexagram.坤.tableNext(41))
    assertSame(Hexagram.師 , Hexagram.坤.tableNext(48))

    assertSame(Hexagram.遯 , Hexagram.坤.tableNext(49))
    assertSame(Hexagram.謙 , Hexagram.坤.tableNext(56))

    assertSame(Hexagram.否 , Hexagram.坤.tableNext(57))
    assertSame(Hexagram.剝 , Hexagram.坤.tableNext(63))
  }

  /**
   * 逆推
   */
  @Test
  fun tableNext_Negative() {
    // ============== 乾 ==============
    assertSame(Hexagram.乾 , Hexagram.乾.tableNext(0))
    assertSame(Hexagram.坤 , Hexagram.乾.tableNext(-1))
    assertSame(Hexagram.剝 , Hexagram.乾.tableNext(-2))
    assertSame(Hexagram.比 , Hexagram.乾.tableNext(-3))
    assertSame(Hexagram.觀 , Hexagram.乾.tableNext(-4))
    assertSame(Hexagram.豫 , Hexagram.乾.tableNext(-5))
    assertSame(Hexagram.晉 , Hexagram.乾.tableNext(-6))
    assertSame(Hexagram.萃 , Hexagram.乾.tableNext(-7))
    assertSame(Hexagram.否 , Hexagram.乾.tableNext(-8))

    assertSame(Hexagram.謙 , Hexagram.乾.tableNext(-9))
    assertSame(Hexagram.艮 , Hexagram.乾.tableNext(-10))
    assertSame(Hexagram.遯 , Hexagram.乾.tableNext(-16))

    assertSame(Hexagram.師 , Hexagram.乾.tableNext(-17))
    assertSame(Hexagram.訟 , Hexagram.乾.tableNext(-24))

    assertSame(Hexagram.升 , Hexagram.乾.tableNext(-25))
    assertSame(Hexagram.姤 , Hexagram.乾.tableNext(-32))

    assertSame(Hexagram.復 , Hexagram.乾.tableNext(-33))
    assertSame(Hexagram.無妄 , Hexagram.乾.tableNext(-40))

    assertSame(Hexagram.明夷 , Hexagram.乾.tableNext(-41))
    assertSame(Hexagram.同人 , Hexagram.乾.tableNext(-48))

    assertSame(Hexagram.臨 , Hexagram.乾.tableNext(-49))
    assertSame(Hexagram.履 , Hexagram.乾.tableNext(-56))

    assertSame(Hexagram.泰 , Hexagram.乾.tableNext(-57))
    assertSame(Hexagram.夬 , Hexagram.乾.tableNext(-63))

    assertSame(Hexagram.乾 , Hexagram.乾.tableNext(-64))


    // ============== 大壯 ==============
    assertSame(Hexagram.大有 , Hexagram.大壯.tableNext(-1))
    assertSame(Hexagram.夬 , Hexagram.大壯.tableNext(-2))
    assertSame(Hexagram.乾 , Hexagram.大壯.tableNext(-3))
    assertSame(Hexagram.坤 , Hexagram.大壯.tableNext(-4))
    assertSame(Hexagram.否 , Hexagram.大壯.tableNext(-11))

    assertSame(Hexagram.謙 , Hexagram.大壯.tableNext(-12))
    assertSame(Hexagram.遯 , Hexagram.大壯.tableNext(-19))

    assertSame(Hexagram.師 , Hexagram.大壯.tableNext(-20))
    assertSame(Hexagram.訟 , Hexagram.大壯.tableNext(-27))

    assertSame(Hexagram.泰 , Hexagram.大壯.tableNext(-60))
    assertSame(Hexagram.小畜 , Hexagram.大壯.tableNext(-63))
    assertSame(Hexagram.大壯 , Hexagram.大壯.tableNext(-64))


    // ============== 坤 ==============

    assertSame(Hexagram.剝 , Hexagram.坤.tableNext(-1))
    assertSame(Hexagram.比 , Hexagram.坤.tableNext(-2))
    assertSame(Hexagram.觀 , Hexagram.坤.tableNext(-3))
    assertSame(Hexagram.豫 , Hexagram.坤.tableNext(-4))
    assertSame(Hexagram.晉 , Hexagram.坤.tableNext(-5))
    assertSame(Hexagram.萃 , Hexagram.坤.tableNext(-6))
    assertSame(Hexagram.否 , Hexagram.坤.tableNext(-7))

    assertSame(Hexagram.謙 , Hexagram.坤.tableNext(-8))
    assertSame(Hexagram.師 , Hexagram.坤.tableNext(-16))
    assertSame(Hexagram.升 , Hexagram.坤.tableNext(-24))
    assertSame(Hexagram.復 , Hexagram.坤.tableNext(-32))
    assertSame(Hexagram.明夷 , Hexagram.坤.tableNext(-40))
    assertSame(Hexagram.臨 , Hexagram.坤.tableNext(-48))

    assertSame(Hexagram.泰 , Hexagram.坤.tableNext(-56))
    assertSame(Hexagram.夬 , Hexagram.坤.tableNext(-62))
    assertSame(Hexagram.乾 , Hexagram.坤.tableNext(-63))

    assertSame(Hexagram.坤 , Hexagram.坤.tableNext(-64))
  }


}
