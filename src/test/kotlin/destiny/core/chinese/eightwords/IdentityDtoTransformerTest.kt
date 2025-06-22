/**
 * Created by smallufo on 2025-06-20.
 */
package destiny.core.chinese.eightwords

import destiny.core.Scale.*
import destiny.core.calendar.eightwords.EightWords
import destiny.core.calendar.eightwords.IdentityPattern
import destiny.core.calendar.eightwords.IdentityPatterns.branchCombined
import destiny.core.calendar.eightwords.IdentityPatterns.branchOpposition
import destiny.core.calendar.eightwords.IdentityPatterns.stemCombined
import destiny.core.calendar.eightwords.IdentityPatterns.stemRooted
import destiny.core.calendar.eightwords.IdentityPatterns.trilogy
import destiny.core.chinese.Branch.*
import destiny.core.chinese.FiveElement
import destiny.core.chinese.Stem.*
import destiny.core.chinese.StemBranch.*
import destiny.core.chinese.eightwords.IdentityDtoTransformer.toBranchCombinedDtos
import destiny.core.chinese.eightwords.IdentityDtoTransformer.toBranchOppositionDtos
import destiny.core.chinese.eightwords.IdentityDtoTransformer.toStemCombinedDtos
import destiny.core.chinese.eightwords.IdentityDtoTransformer.toStemRootedDtos
import destiny.core.chinese.eightwords.IdentityDtoTransformer.toTrilogyDtos
import destiny.core.electional.Ew.*
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals

class IdentityDtoTransformerTest {


  @Nested
  inner class 天干五合 {

    @Test
    fun single() {
      val ew = EightWords(丁丑, 壬子, 癸未, 己未)
      val dtos = with(stemCombined) {
        ew.getPatterns()
      }.map { it as IdentityPattern.StemCombined }
        .toStemCombinedDtos()

      val expected = setOf(
        EwIdentity.StemCombinedDto(
          "年干(丁) 與 月干(壬) 合木",
          setOf(
            NatalStems(setOf(YEAR), 丁),
            NatalStems(setOf(MONTH), 壬)
          ),
          FiveElement.木
        )
      )
      assertEquals(expected, dtos)
    }

    @Test
    fun multiple() {
      val ew = EightWords(丁丑, 壬子, 癸未, 戊午)
      val dtos = with(stemCombined) {
        ew.getPatterns()
      }.map { it as IdentityPattern.StemCombined }
        .toStemCombinedDtos()

      val expected = setOf(
        EwIdentity.StemCombinedDto(
          "年干(丁) 與 月干(壬) 合木",
          setOf(
            NatalStems(setOf(YEAR), 丁),
            NatalStems(setOf(MONTH), 壬)
          ),
          FiveElement.木
        ),
        EwIdentity.StemCombinedDto(
          "日干(癸) 與 時干(戊) 合火",
          setOf(
            NatalStems(setOf(DAY), 癸),
            NatalStems(setOf(HOUR), 戊)
          ),
          FiveElement.火
        )
      )
      assertEquals(expected, dtos)
    }

    @Test
    fun multipleCombined1() {
      val ew = EightWords(丁丑, 壬子, 癸未, 壬戌)
      val dtos = with(stemCombined) {
        ew.getPatterns()
      }.map { it as IdentityPattern.StemCombined }
        .toStemCombinedDtos()

      val expected = setOf(
        EwIdentity.StemCombinedDto(
          "年干(丁) 與 月干、時干(均為壬) 合木",
          setOf(
            NatalStems(setOf(YEAR), 丁),
            NatalStems(setOf(MONTH, HOUR), 壬)
          ),
          FiveElement.木
        )
      )
      assertEquals(expected, dtos)
    }

    @Test
    fun multipleCombined2() {
      val ew = EightWords(丁丑, 壬子, 丁丑, 壬子)
      val dtos = with(stemCombined) {
        ew.getPatterns()
      }.map { it as IdentityPattern.StemCombined }
        .toStemCombinedDtos()

      val expected = setOf(
        EwIdentity.StemCombinedDto(
          "年干、日干(均為丁) 與 月干、時干(均為壬) 合木",
          setOf(
            NatalStems(setOf(YEAR, DAY), 丁),
            NatalStems(setOf(MONTH, HOUR), 壬)
          ),
          FiveElement.木
        )
      )
      assertEquals(expected, dtos)
    }
  }

  @Nested
  inner class 地支六合 {

    @Test
    fun isolated() {
      val ew = EightWords(丙子, 辛丑, 癸亥, 甲寅)
      val dtos = with(branchCombined) {
        ew.getPatterns()
      }.map { it as IdentityPattern.BranchCombined }
        .toBranchCombinedDtos()

      val expected = setOf(
        EwIdentity.BranchCombinedDto(
          "年支(子) 六合 月支(丑)",
          setOf(
            NatalBranches(setOf(YEAR), 子),
            NatalBranches(setOf(MONTH), 丑)
          )
        ),
        EwIdentity.BranchCombinedDto(
          "日支(亥) 六合 時支(寅)",
          setOf(
            NatalBranches(setOf(DAY), 亥),
            NatalBranches(setOf(HOUR), 寅)
          )
        )
      )
      assertEquals(expected, dtos)
    }

    @Test
    fun merged_1_3() {
      val ew = EightWords(丙子, 辛丑, 乙丑, 丁丑)
      val dtos = with(branchCombined) {
        ew.getPatterns()
      }.map { it as IdentityPattern.BranchCombined }
        .toBranchCombinedDtos()

      val expected = setOf(
        EwIdentity.BranchCombinedDto(
          "年支(子) 六合 月支、日支、時支(均為 丑)",
          setOf(
            NatalBranches(setOf(YEAR), 子),
            NatalBranches(setOf(MONTH, DAY, HOUR), 丑)
          )
        )
      )
      assertEquals(expected, dtos)
    }

    @Test
    fun merged_3_1() {
      val ew = EightWords(丙子, 庚子, 辛丑, 戊子)
      val dtos = with(branchCombined) {
        ew.getPatterns()
      }.map { it as IdentityPattern.BranchCombined }
        .toBranchCombinedDtos()

      val expected = setOf(
        EwIdentity.BranchCombinedDto(
          "年支、月支、時支(均為 子) 六合 日支(丑)",
          setOf(
            NatalBranches(setOf(YEAR, MONTH, HOUR), 子),
            NatalBranches(setOf(DAY), 丑)
          )
        )
      )
      assertEquals(expected, dtos)
    }

    @Test
    fun merged_2_2() {
      val ew = EightWords(丙子, 庚子, 辛丑, 己丑)
      val dtos = with(branchCombined) {
        ew.getPatterns()
      }.map { it as IdentityPattern.BranchCombined }
        .toBranchCombinedDtos()

      val expected = setOf(
        EwIdentity.BranchCombinedDto(
          "年支、月支(均為 子) 六合 日支、時支(均為 丑)",
          setOf(
            NatalBranches(setOf(YEAR, MONTH), 子),
            NatalBranches(setOf(DAY, HOUR), 丑)
          )
        )
      )
      assertEquals(expected, dtos)
    }

  }

  @Nested
  inner class 地支三合 {

    @Test
    fun single() {
      val ew = EightWords(己卯, 辛未, 丁亥, 乙巳)
      val dtos = with(trilogy) {
        ew.getPatterns()
      }.map { it as IdentityPattern.Trilogy }
        .toTrilogyDtos()

      val expected = setOf(
        EwIdentity.TrilogyDto(
          "年支、月支、日支三合木局",
          setOf(
            NatalBranches(setOf(YEAR), 卯),
            NatalBranches(setOf(MONTH), 未),
            NatalBranches(setOf(DAY), 亥)
          ),
          FiveElement.木
        )
      )
      assertEquals(expected, dtos)
    }

    @Test
    fun multiple() {
      val ew = EightWords(己卯, 辛未, 丁亥, 癸卯)
      val dtos = with(trilogy) {
        ew.getPatterns()
      }.map { it as IdentityPattern.Trilogy }
        .toTrilogyDtos()

      val expected = setOf(
        EwIdentity.TrilogyDto(
          "年支、月支、日支三合木局",
          setOf(
            NatalBranches(setOf(YEAR), 卯),
            NatalBranches(setOf(MONTH), 未),
            NatalBranches(setOf(DAY), 亥)
          ),
          FiveElement.木
        ),
        EwIdentity.TrilogyDto(
          "月支、日支、時支三合木局",
          setOf(
            NatalBranches(setOf(MONTH), 未),
            NatalBranches(setOf(DAY), 亥),
            NatalBranches(setOf(HOUR), 卯)
          ),
          FiveElement.木
        )
      )
      assertEquals(expected, dtos)
    }
  }

  @Nested
  inner class 地支正沖 {

    @Test
    fun isolated() {
      val ew = EightWords(丁丑, 壬子, 癸未, 戊午)
      val dtos = with(branchOpposition) {
        ew.getPatterns()
      }.map { it as IdentityPattern.BranchOpposition }
        .toBranchOppositionDtos()

      val expected = setOf(
        EwIdentity.BranchOppositionDto(
          "年支(丑) 正沖 日支(未)",
          setOf(
            NatalBranches(setOf(YEAR), 丑),
            NatalBranches(setOf(DAY), 未)
          )
        ),
        EwIdentity.BranchOppositionDto(
          "月支(子) 正沖 時支(午)",
          setOf(
            NatalBranches(setOf(MONTH), 子),
            NatalBranches(setOf(HOUR), 午)
          )
        )
      )
      assertEquals(expected, dtos)
    }

    @Test
    fun merged1() {
      val ew = EightWords(癸未, 乙丑, 辛丑, 己丑)
      val dtos = with(branchOpposition) {
        ew.getPatterns()
      }.map { it as IdentityPattern.BranchOpposition }
        .toBranchOppositionDtos()

      val expected = setOf(
        EwIdentity.BranchOppositionDto(
          "年支(未) 正沖 月支、日支、時支(均為 丑)",
          setOf(
            NatalBranches(setOf(YEAR), 未),
            NatalBranches(setOf(MONTH, DAY, HOUR), 丑)
          )
        )
      )
      assertEquals(expected, dtos)
    }

    @Test
    fun merged2() {
      val ew = EightWords(癸未, 乙丑, 辛丑, 乙未)
      val dtos = with(branchOpposition) {
        ew.getPatterns()
      }.map { it as IdentityPattern.BranchOpposition }
        .toBranchOppositionDtos()

      val expected = setOf(
        EwIdentity.BranchOppositionDto(
          "年支、時支(均為 未) 正沖 月支、日支(均為 丑)",
          setOf(
            NatalBranches(setOf(YEAR, HOUR), 未),
            NatalBranches(setOf(MONTH, DAY), 丑)
          )
        )
      )
      assertEquals(expected, dtos)
    }
  }

  @Nested
  inner class 天干通根 {

    @Test
    fun single() {
      val ew = EightWords(庚辰, 戊寅, 甲午, 癸酉)
      val dtos = with(stemRooted) {
        ew.getPatterns()
      }.map { it as IdentityPattern.StemRooted }
        .toStemRootedDtos()

      val expected = setOf(
        EwIdentity.StemRootedDto(
          "月干(戊) 通根 年支(辰)、月支(寅)",
          setOf(
            NatalStems(setOf(MONTH), 戊)
          ),
          setOf(
            NatalBranches(setOf(YEAR), 辰),
            NatalBranches(setOf(MONTH), 寅)
          )
        ),
        EwIdentity.StemRootedDto(
          "日干(甲) 通根 月支(寅)",
          setOf(
            NatalStems(setOf(DAY), 甲)
          ),
          setOf(
            NatalBranches(setOf(MONTH), 寅)
          )
        ),
        EwIdentity.StemRootedDto(
          "時干(癸) 通根 年支(辰)",
          setOf(
            NatalStems(setOf(HOUR), 癸)
          ),
          setOf(
            NatalBranches(setOf(YEAR), 辰)
          )
        )
      )
      assertEquals(expected, dtos)
    }

    @Test
    fun multiple_branchMerged() {
      val ew = EightWords(丁丑, 壬子, 癸未, 戊午)
      val dtos = with(stemRooted) {
        ew.getPatterns()
      }.map { it as IdentityPattern.StemRooted }
        .toStemRootedDtos()

      val expected = setOf(
        EwIdentity.StemRootedDto(
          "年干(丁) 通根 日支(未)、時支(午)",
          setOf(
            NatalStems(setOf(YEAR), 丁)
          ),
          setOf(
            NatalBranches(setOf(DAY), 未),
            NatalBranches(setOf(HOUR), 午)
          )
        ),
        EwIdentity.StemRootedDto(
          "日干(癸) 通根 年支(丑)、月支(子)",
          setOf(
            NatalStems(setOf(DAY), 癸)
          ),
          setOf(
            NatalBranches(setOf(YEAR), 丑),
            NatalBranches(setOf(MONTH), 子)
          )
        )
      )
      assertEquals(expected, dtos)
    }

    @Test
    fun multiple_stemMerged() {
      val ew = EightWords(丁丑, 壬子, 癸未, 丁巳)
      val dtos = with(stemRooted) {
        ew.getPatterns()
      }.map { it as IdentityPattern.StemRooted }
        .toStemRootedDtos()

      val expected = setOf(
        EwIdentity.StemRootedDto(
          "年干、時干(均為 丁) 通根 日支(未)",
          setOf(
            NatalStems(setOf(YEAR, HOUR), 丁)
          ),
          setOf(
            NatalBranches(setOf(DAY), 未)
          )
        ),
        EwIdentity.StemRootedDto(
          "日干(癸) 通根 年支(丑)、月支(子)",
          setOf(
            NatalStems(setOf(DAY), 癸)
          ),
          setOf(
            NatalBranches(setOf(YEAR), 丑),
            NatalBranches(setOf(MONTH), 子)
          )
        )
      )
      assertEquals(expected, dtos)
    }
  }

}

