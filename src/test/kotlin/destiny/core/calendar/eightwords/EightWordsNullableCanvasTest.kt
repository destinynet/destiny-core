/**
 * Created by smallufo on 2018-02-21.
 */
package destiny.core.calendar.eightwords

import destiny.core.chinese.Branch
import destiny.core.chinese.StemBranch
import destiny.core.chinese.StemBranchOptional
import kotlin.test.Test
import kotlin.test.assertTrue

class EightWordsNullableCanvasTest {

  @Test
  fun 完整八字_右至左() {
    val ew = EightWords(StemBranch.甲子, StemBranch.乙丑, StemBranch.丙寅, StemBranch.丁卯)
    EightWordsNullableCanvas(ew).htmlOutput.also {
      assertTrue(it.contains("丁丙乙甲"))
      assertTrue(it.contains("卯寅丑子"))
    }
  }

  @Test
  fun 完整八字_左至右() {
    val ew = EightWords(StemBranch.甲子, StemBranch.乙丑, StemBranch.丙寅, StemBranch.丁卯)
    EightWordsNullableCanvas(ew, Direction.L2R).htmlOutput.also {
      assertTrue(it.contains("甲乙丙丁"))
      assertTrue(it.contains("子丑寅卯"))
    }
  }

  @Test
  fun 不完整_只有月支以及日柱_R2L() {
    val ewn = EightWordsNullable.of(StemBranchOptional.empty(), StemBranchOptional[null, Branch.丑], StemBranch.丙寅, StemBranchOptional.empty())
    EightWordsNullableCanvas(ewn, Direction.R2L).also {
      assertTrue(it.toString().contains("　丙　　"))
      assertTrue(it.toString().contains("　寅丑　"))
    }

  }

  @Test
  fun 不完整_只有月支以及日柱_L2R() {
    val ewn = EightWordsNullable.of(StemBranchOptional.empty(), StemBranchOptional[null, Branch.丑], StemBranch.丙寅, StemBranchOptional.empty())
    EightWordsNullableCanvas(ewn, Direction.L2R).also {
      assertTrue(it.toString().contains("　　丙　"))
      assertTrue(it.toString().contains("　丑寅　"))
    }
  }
}
