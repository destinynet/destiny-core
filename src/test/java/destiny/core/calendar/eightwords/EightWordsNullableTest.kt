package destiny.core.calendar.eightwords

import destiny.core.chinese.Branch.*
import destiny.core.chinese.Stem.*
import destiny.core.chinese.StemBranchOptional
import org.junit.Assert.assertEquals
import kotlin.test.Test

class EightWordsNullableTest {

  @Test
  fun testEquals() {
    var ew1 = EightWordsNullable.empty()
    var ew2 = EightWordsNullable.empty()
    assertEquals(ew1, ew2) //兩個都是 null 八字,應該 equals

    ew1 = EightWordsNullable(StemBranchOptional["甲子"], StemBranchOptional["乙丑"], StemBranchOptional["丙寅"],
                             StemBranchOptional["丁卯"])
    ew2 = EightWordsNullable(StemBranchOptional["甲子"], StemBranchOptional["乙丑"], StemBranchOptional["丙寅"],
                             StemBranchOptional["丁卯"])
    assertEquals(ew1, ew2)

    ew1 = EightWordsNullable(StemBranchOptional["甲子"], StemBranchOptional["乙丑"], StemBranchOptional["丙寅"],
                             StemBranchOptional["丁卯"])
    ew2 = EightWordsNullable(
      StemBranchOptional[甲, 子],
      StemBranchOptional[乙, 丑],
      StemBranchOptional[丙, 寅],
      StemBranchOptional[丁, 卯])
    assertEquals(ew1, ew2)

    ew1 = EightWordsNullable(StemBranchOptional["甲子"], StemBranchOptional.empty(), StemBranchOptional["丙寅"],
                             StemBranchOptional.empty())
    ew2 = EightWordsNullable(
      StemBranchOptional[甲, 子],
      StemBranchOptional.empty(),
      StemBranchOptional[丙, 寅],
      StemBranchOptional.empty())
    assertEquals(ew1, ew2)


    ew1 = EightWordsNullable(StemBranchOptional["甲子"],
                             StemBranchOptional.empty(),
                             StemBranchOptional[丙, null],
                             StemBranchOptional[null, 卯]
                            )
    ew2 = EightWordsNullable(
      StemBranchOptional[甲, 子],
      StemBranchOptional.empty(),
      StemBranchOptional[丙, null],
      StemBranchOptional[null, 卯])
    assertEquals(ew1, ew2)

  }

}