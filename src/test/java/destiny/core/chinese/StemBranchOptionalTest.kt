/**
 * Created by smallufo on 2015-05-26.
 */
package destiny.core.chinese

import destiny.core.chinese.Branch.*
import destiny.core.chinese.Stem.*
import kotlin.test.*

class StemBranchOptionalTest {

  @Test
  fun testCheck_passed() {
    StemBranchOptional[甲, 子].let {
      assertEquals(StemBranch.甲子 , it)
    }
    StemBranchOptional["甲子"].let {
      assertEquals(StemBranch.甲子 , it)
    }

    StemBranchOptional['甲', '子'].let {
      assertEquals(StemBranch.甲子 , it)
    }
    StemBranchOptional[甲, 寅].let {
      assertEquals(StemBranch.甲寅 , it)
    }

    StemBranchOptional[甲, null].also { assertNotNull(it) }
    StemBranchOptional[癸, 丑].let {
      assertEquals(StemBranch.癸丑 , it)
    }
    StemBranchOptional[癸, 亥].let { assertEquals(StemBranch.癸亥, it) }
    StemBranchOptional[null, 亥].let { assertNotNull(it) }
    StemBranchOptional[null, null].let { assertNotNull(it) }
  }

  @Test
  fun testCheck_failed() {
    try {
      // 錯誤的組合
      StemBranchOptional[甲, 丑]
      fail("error")
    } catch (e: RuntimeException) {
      assertTrue(true)
    }

    try {
      // 錯誤的組合
      StemBranchOptional["甲丑"]
      fail("error")
    } catch (e: RuntimeException) {
      assertTrue(true)
    }

    try {
      // 錯誤的組合
      StemBranchOptional['甲', '丑']
      fail("error")
    } catch (e: RuntimeException) {
      assertTrue(true)
    }


    try {
      // 錯誤的組合
      StemBranchOptional[甲, 丑]
      fail("error")
    } catch (e: RuntimeException) {
      assertTrue(true)
    }

  }


  @Test
  fun testSame() {
    assertSame(StemBranchOptional["甲子"], StemBranchOptional[甲, 子])
    assertSame(StemBranchOptional["甲子"], StemBranchOptional['甲', '子'])
    assertSame(StemBranchOptional['甲', '子'], StemBranchOptional[甲, 子])
    assertSame(StemBranchOptional[甲, 子], StemBranchOptional[甲, 子])

    assertNotSame(StemBranchOptional["甲子"], StemBranchOptional['甲', '寅'])
  }

  @Test
  fun testGetIndex() {
    assertEquals(0 , StemBranchOptional[甲, 子].index)
    assertEquals(59, StemBranchOptional[癸, 亥].index)

    assertNull(StemBranchOptional[甲, null].index)
    assertNull(StemBranchOptional[null, 子].index)
  }

  @Test
  fun testNext() {
    assertSame(StemBranchOptional[乙, 丑], StemBranchOptional[甲, 子].next(1))
  }

  @Test
  fun printAll() {
    StemBranchOptional.iterator().forEach { println(it) }
  }

}