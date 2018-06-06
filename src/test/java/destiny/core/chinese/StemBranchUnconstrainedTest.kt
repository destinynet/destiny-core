/**
 * Created by smallufo on 2018-06-06.
 */
package destiny.core.chinese

import kotlin.test.Test
import kotlin.test.assertSame


class StemBranchUnconstrainedTest {

  @Test
  fun getIndex() {
    assertSame(0 , StemBranchUnconstrained.甲子.index)
    assertSame(119 , StemBranchUnconstrained.甲亥.index)
  }

  @Test
  fun testNext() {
    assertSame(StemBranchUnconstrained.乙子 , StemBranchUnconstrained.甲子.next) // 0 -> 1
    assertSame(StemBranchUnconstrained.甲子 , StemBranchUnconstrained.甲亥.next) // 119 -> 0
  }

  @Test
  fun testPrev() {
    assertSame(StemBranchUnconstrained.甲亥 , StemBranchUnconstrained.甲子.previous ) // 0 -> 119
    assertSame(StemBranchUnconstrained.甲子 , StemBranchUnconstrained.乙子.previous ) // 1 -> 0
  }

  @Test
  fun testAheadOf() {
    assertSame(1 , StemBranchUnconstrained.甲子.getAheadOf(StemBranchUnconstrained.甲亥))
    assertSame(119 , StemBranchUnconstrained.甲亥.getAheadOf(StemBranchUnconstrained.甲子))

    assertSame(1 , StemBranchUnconstrained.乙子.getAheadOf(StemBranchUnconstrained.甲子))
    assertSame(119 , StemBranchUnconstrained.甲子.getAheadOf(StemBranchUnconstrained.乙子))
  }

  @Test
  fun testToStemBranch() {
    assertSame(StemBranch.甲子 , StemBranchUnconstrained.甲子.toStemBranch())
    assertSame(StemBranch.癸亥 , StemBranchUnconstrained.癸亥.toStemBranch())
  }
}