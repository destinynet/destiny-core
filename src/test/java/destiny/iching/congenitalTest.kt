/**
 * Created by smallufo on 2019-04-26.
 */
package destiny.iching

import mu.KotlinLogging
import kotlin.test.Test
import kotlin.test.assertSame
import destiny.iching.congenital.next
import destiny.iching.congenital.toHexagram
import destiny.iching.congenital.toInt
import destiny.iching.congenital.timeSeq
import kotlin.test.assertEquals


class congenitalTest {

  val logger = KotlinLogging.logger {  }

  @Test
  fun testTimeSeq_順推() {
    generateSequence(Hexagram.乾) {
      it.timeSeq(true)
    }.take(64)
      .forEach {
        logger.info("{}" , it)
      }
  }

  @Test
  fun testTimeSeq_逆推() {
    generateSequence(Hexagram.乾) {
      it.timeSeq(false)
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
      it.first.next() to it.second+1
    }.take(64)
      .forEach {
        logger.info("{}" , it)
      }
  }


  @Test
  fun next() {
    assertSame(Hexagram.夬 , congenital.next1(Hexagram.乾))
    assertSame(Hexagram.大有 , congenital.next1(Hexagram.夬))
    assertSame(Hexagram.履 , congenital.next1(Hexagram.泰))
    assertSame(Hexagram.坤 , congenital.next1(Hexagram.剝))
    assertSame(Hexagram.乾 , congenital.next1(Hexagram.坤))
  }

  @Test
  fun prev() {
    assertSame(Hexagram.乾 , congenital.prev1(Hexagram.夬))
    assertSame(Hexagram.夬 , congenital.prev1(Hexagram.大有))
    assertSame(Hexagram.泰 , congenital.prev1(Hexagram.履))
    assertSame(Hexagram.剝 , congenital.prev1(Hexagram.坤))
    assertSame(Hexagram.坤 , congenital.prev1(Hexagram.乾))
  }

  /**
   * 順推
   */
  @Test
  fun next_Positive() {
    // ============== 乾 ==============
    assertSame(Hexagram.乾 , Hexagram.乾.next(0))
    assertSame(Hexagram.夬 , Hexagram.乾.next(1))
    assertSame(Hexagram.大有 , Hexagram.乾.next(2))
    assertSame(Hexagram.大壯 , Hexagram.乾.next(3))
    assertSame(Hexagram.小畜 , Hexagram.乾.next(4))
    assertSame(Hexagram.需 , Hexagram.乾.next(5))
    assertSame(Hexagram.大畜 , Hexagram.乾.next(6))
    assertSame(Hexagram.泰 , Hexagram.乾.next(7))

    assertSame(Hexagram.履 , Hexagram.乾.next(8))
    assertSame(Hexagram.臨 , Hexagram.乾.next(15))

    assertSame(Hexagram.同人 , Hexagram.乾.next(16))
    assertSame(Hexagram.無妄 , Hexagram.乾.next(24))
    assertSame(Hexagram.姤 , Hexagram.乾.next(32))
    assertSame(Hexagram.訟 , Hexagram.乾.next(40))
    assertSame(Hexagram.遯 , Hexagram.乾.next(48))

    assertSame(Hexagram.否 , Hexagram.乾.next(56))
    assertSame(Hexagram.坤 , Hexagram.乾.next(63))

    // ============== 大壯 ==============

    assertSame(Hexagram.小畜 , Hexagram.大壯.next(1))
    assertSame(Hexagram.泰 , Hexagram.大壯.next(4))
    assertSame(Hexagram.履 , Hexagram.大壯.next(5))
    assertSame(Hexagram.臨 , Hexagram.大壯.next(12))

    // ============== 震 ==============

    assertSame(Hexagram.益 , Hexagram.震.next(1))
    assertSame(Hexagram.復 , Hexagram.震.next(4))
    assertSame(Hexagram.姤 , Hexagram.震.next(5))
    assertSame(Hexagram.巽 , Hexagram.震.next(9))

    // ============== 坤 ==============

    assertSame(Hexagram.乾 , Hexagram.坤.next(1))
    assertSame(Hexagram.夬 , Hexagram.坤.next(2))
    assertSame(Hexagram.泰 , Hexagram.坤.next(8))

    assertSame(Hexagram.履 , Hexagram.坤.next(9))
    assertSame(Hexagram.臨 , Hexagram.坤.next(16))

    assertSame(Hexagram.同人 , Hexagram.坤.next(17))
    assertSame(Hexagram.明夷 , Hexagram.坤.next(24))

    assertSame(Hexagram.無妄 , Hexagram.坤.next(25))
    assertSame(Hexagram.復 , Hexagram.坤.next(32))

    assertSame(Hexagram.姤 , Hexagram.坤.next(33))
    assertSame(Hexagram.升 , Hexagram.坤.next(40))

    assertSame(Hexagram.訟 , Hexagram.坤.next(41))
    assertSame(Hexagram.師 , Hexagram.坤.next(48))

    assertSame(Hexagram.遯 , Hexagram.坤.next(49))
    assertSame(Hexagram.謙 , Hexagram.坤.next(56))

    assertSame(Hexagram.否 , Hexagram.坤.next(57))
    assertSame(Hexagram.剝 , Hexagram.坤.next(63))
  }

  /**
   * 逆推
   */
  @Test
  fun next_Negative() {
    // ============== 乾 ==============
    assertSame(Hexagram.乾 , Hexagram.乾.next(0))
    assertSame(Hexagram.坤 , Hexagram.乾.next(-1))
    assertSame(Hexagram.剝 , Hexagram.乾.next(-2))
    assertSame(Hexagram.比 , Hexagram.乾.next(-3))
    assertSame(Hexagram.觀 , Hexagram.乾.next(-4))
    assertSame(Hexagram.豫 , Hexagram.乾.next(-5))
    assertSame(Hexagram.晉 , Hexagram.乾.next(-6))
    assertSame(Hexagram.萃 , Hexagram.乾.next(-7))
    assertSame(Hexagram.否 , Hexagram.乾.next(-8))

    assertSame(Hexagram.謙 , Hexagram.乾.next(-9))
    assertSame(Hexagram.艮 , Hexagram.乾.next(-10))
    assertSame(Hexagram.遯 , Hexagram.乾.next(-16))

    assertSame(Hexagram.師 , Hexagram.乾.next(-17))
    assertSame(Hexagram.訟 , Hexagram.乾.next(-24))

    assertSame(Hexagram.升 , Hexagram.乾.next(-25))
    assertSame(Hexagram.姤 , Hexagram.乾.next(-32))

    assertSame(Hexagram.復 , Hexagram.乾.next(-33))
    assertSame(Hexagram.無妄 , Hexagram.乾.next(-40))

    assertSame(Hexagram.明夷 , Hexagram.乾.next(-41))
    assertSame(Hexagram.同人 , Hexagram.乾.next(-48))

    assertSame(Hexagram.臨 , Hexagram.乾.next(-49))
    assertSame(Hexagram.履 , Hexagram.乾.next(-56))

    assertSame(Hexagram.泰 , Hexagram.乾.next(-57))
    assertSame(Hexagram.夬 , Hexagram.乾.next(-63))

    assertSame(Hexagram.乾 , Hexagram.乾.next(-64))


    // ============== 大壯 ==============
    assertSame(Hexagram.大有 , Hexagram.大壯.next(-1))
    assertSame(Hexagram.夬 , Hexagram.大壯.next(-2))
    assertSame(Hexagram.乾 , Hexagram.大壯.next(-3))
    assertSame(Hexagram.坤 , Hexagram.大壯.next(-4))
    assertSame(Hexagram.否 , Hexagram.大壯.next(-11))

    assertSame(Hexagram.謙 , Hexagram.大壯.next(-12))
    assertSame(Hexagram.遯 , Hexagram.大壯.next(-19))

    assertSame(Hexagram.師 , Hexagram.大壯.next(-20))
    assertSame(Hexagram.訟 , Hexagram.大壯.next(-27))

    assertSame(Hexagram.泰 , Hexagram.大壯.next(-60))
    assertSame(Hexagram.小畜 , Hexagram.大壯.next(-63))
    assertSame(Hexagram.大壯 , Hexagram.大壯.next(-64))


    // ============== 坤 ==============

    assertSame(Hexagram.剝 , Hexagram.坤.next(-1))
    assertSame(Hexagram.比 , Hexagram.坤.next(-2))
    assertSame(Hexagram.觀 , Hexagram.坤.next(-3))
    assertSame(Hexagram.豫 , Hexagram.坤.next(-4))
    assertSame(Hexagram.晉 , Hexagram.坤.next(-5))
    assertSame(Hexagram.萃 , Hexagram.坤.next(-6))
    assertSame(Hexagram.否 , Hexagram.坤.next(-7))

    assertSame(Hexagram.謙 , Hexagram.坤.next(-8))
    assertSame(Hexagram.師 , Hexagram.坤.next(-16))
    assertSame(Hexagram.升 , Hexagram.坤.next(-24))
    assertSame(Hexagram.復 , Hexagram.坤.next(-32))
    assertSame(Hexagram.明夷 , Hexagram.坤.next(-40))
    assertSame(Hexagram.臨 , Hexagram.坤.next(-48))

    assertSame(Hexagram.泰 , Hexagram.坤.next(-56))
    assertSame(Hexagram.夬 , Hexagram.坤.next(-62))
    assertSame(Hexagram.乾 , Hexagram.坤.next(-63))

    assertSame(Hexagram.坤 , Hexagram.坤.next(-64))
  }


}