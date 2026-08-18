/**
 * Created by smallufo on 2015-05-26.
 */
package destiny.core.chinese

import destiny.core.chinese.Branch.*
import destiny.core.chinese.Stem.*
import kotlin.test.*

class StemBranchOptionalTest {

  /**
   * 合法組合（含只有一半、甚至兩邊皆空）都建得起來，且 [StemBranchOptional.index] 語意正確。
   *
   * 原本這裡只 `println` 一個物件，其餘斷言全被註解掉 ——
   * 而註解掉的那份是拿 [StemBranch] 來比對的，兩者刻意不相等
   * （見 `StemBranchTest.testEqualWithStemBranchOptional`），放回去也是紅的。
   */
  @Test
  fun testCheck_passed() {
    StemBranchOptional[甲, 子].also {
      assertEquals(甲, it.stem)
      assertEquals(子, it.branch)
      assertEquals(0, it.index)
    }

    // 只有一半、甚至兩邊都空，都是合法的，但取不到 index
    StemBranchOptional[甲, null].also {
      assertEquals(甲, it.stem)
      assertNull(it.branch)
      assertNull(it.index)
    }
    StemBranchOptional[null, 子].also {
      assertNull(it.stem)
      assertEquals(子, it.branch)
      assertNull(it.index)
    }
    StemBranchOptional[null, null].also {
      assertNull(it.stem)
      assertNull(it.branch)
      assertNull(it.index)
    }
  }

  /**
   * **不合法的干支組合並不會拋例外。** 陽干只配陽支、陰干只配陰支，「甲丑」湊不出 60 甲子中的任何一柱；
   * 但 [StemBranchOptional.get] 對配不出來的組合會退回 `StemBranchOptional(stem, branch)`，
   * 只是 [StemBranchOptional.index] 取不到（為 null）。
   *
   * 原測試名為 `testCheck_failed`，寫成
   * `try { StemBranchOptional[甲, 丑]; fail("error") } catch (e: Throwable) { assertTrue(true) }` ——
   * `fail()` 丟出的 `AssertionError` 自己也被 `catch (Throwable)` 吞掉，
   * 於是**有沒有拋例外都會綠**，等於什麼都沒驗。這裡改成斷言現況。
   */
  @Test
  fun `不合法的干支組合不會拋例外，只是沒有 index`() {
    listOf(
      StemBranchOptional[甲, 丑],
      StemBranchOptional["甲丑"],
      StemBranchOptional['甲', '丑'],
    ).forEach { sb ->
      assertEquals(甲, sb.stem)
      assertEquals(丑, sb.branch)
      assertNull(sb.index, "$sb 不在 60 甲子之內，index 應為 null")
    }

    // 真正會擋下來的是「長度不對」
    assertFailsWith<IllegalArgumentException> { StemBranchOptional["甲"] }
  }

  @Test
  fun testEquals() {
    assertEquals(StemBranchOptional["甲子"], StemBranchOptional[甲, 子])
    assertEquals(StemBranchOptional["甲子"], StemBranchOptional['甲', '子'])
    assertEquals(StemBranchOptional['甲', '子'], StemBranchOptional[甲, 子])
    assertEquals(StemBranchOptional[甲, 子], StemBranchOptional[甲, 子])

    assertNotEquals(StemBranchOptional["甲子"], StemBranchOptional['甲', '寅'])
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
    assertEquals(StemBranchOptional[乙, 丑], StemBranchOptional[甲, 子].next(1))
    assertSame(StemBranchOptional[乙, 丑], StemBranchOptional[甲, 子].next(1))
  }

  /** 60 甲子從甲子到癸亥，剛好繞一圈。原本只是把 60 個物件 println 出來 */
  @Test
  fun printAll() {
    val all = StemBranchOptional.iterator().asSequence().toList()

    assertEquals(60, all.size)
    assertEquals(StemBranchOptional[甲, 子], all.first())
    assertEquals(StemBranchOptional[癸, 亥], all.last())
    assertEquals(60, all.distinct().size)
    assertEquals((0..59).toList(), all.map { it.index })
  }

}
