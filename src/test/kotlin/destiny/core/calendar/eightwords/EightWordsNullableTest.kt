package destiny.core.calendar.eightwords

import destiny.core.chinese.Branch.*
import destiny.core.chinese.Stem.*
import destiny.core.chinese.StemBranch
import destiny.core.chinese.StemBranchOptional
import kotlin.test.Test
import kotlin.test.assertEquals

class EightWordsNullableTest {

  /** 兩個都是 null 八字,應該 equals */
  @Test
  fun emptyEquals() {
    val ew1 = EightWordsNullable.empty()
    val ew2 = EightWordsNullable.empty()
    assertEquals(ew1, ew2)
  }

  @Test
  fun fullNonNullableInnerEquals() {
    val ew1 = EightWordsNullable.of(StemBranchOptional["甲子"], StemBranchOptional["乙丑"], StemBranchOptional["丙寅"], StemBranchOptional["丁卯"])
    val ew2 = EightWordsNullable.of(StemBranchOptional["甲子"], StemBranchOptional["乙丑"], StemBranchOptional["丙寅"], StemBranchOptional["丁卯"])
    assertEquals(ew1, ew2)
  }

  /**
   * 確保 [EightWordsNullable] 會轉換成 [EightWords]
   */
  @Test
  fun fullNonNullableEqualsEightWords() {
    val ew1 = EightWordsNullable.of(StemBranchOptional["甲子"], StemBranchOptional["乙丑"], StemBranchOptional["丙寅"], StemBranchOptional["丁卯"])
    val ew2 = EightWords(StemBranch[StemBranch.甲子], StemBranch[StemBranch.乙丑], StemBranch[StemBranch.丙寅], StemBranch[StemBranch.丁卯])
    assertEquals(ew1, ew2)
  }

  @Test
  fun testEquals() {

    var ew1 = EightWordsNullable.of(StemBranchOptional["甲子"], StemBranchOptional["乙丑"], StemBranchOptional["丙寅"], StemBranchOptional["丁卯"])
    var ew2 = EightWordsNullable.of(StemBranchOptional[甲, 子], StemBranchOptional[乙, 丑], StemBranchOptional[丙, 寅], StemBranchOptional[丁, 卯])
    assertEquals(ew1, ew2)

    ew1 = EightWordsNullable.of(StemBranchOptional["甲子"], StemBranchOptional.empty(), StemBranchOptional["丙寅"], StemBranchOptional.empty())
    ew2 = EightWordsNullable.of(StemBranchOptional[甲, 子], StemBranchOptional.empty(), StemBranchOptional[丙, 寅], StemBranchOptional.empty())
    assertEquals(ew1, ew2)


    ew1 = EightWordsNullable.of(StemBranchOptional["甲子"], StemBranchOptional.empty(), StemBranchOptional[丙, null], StemBranchOptional[null, 卯])
    ew2 = EightWordsNullable.of(StemBranchOptional[甲, 子], StemBranchOptional.empty(), StemBranchOptional[丙, null], StemBranchOptional[null, 卯])
    assertEquals(ew1, ew2)

  }

}
