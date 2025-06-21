/**
 * Created by smallufo on 2025-06-21.
 */
package destiny.core.chinese.eightwords

import destiny.core.FlowScale
import destiny.core.Scale.*
import destiny.core.calendar.eightwords.*
import destiny.core.calendar.eightwords.FlowLargePatterns.trilogyToFlow
import destiny.core.calendar.eightwords.FlowLargeYearPatterns.toFlowTrilogy
import destiny.core.calendar.eightwords.FlowYearMonthPatterns.affecting
import destiny.core.chinese.Branch.*
import destiny.core.chinese.FiveElement
import destiny.core.chinese.Stem.*
import destiny.core.chinese.StemBranch.*
import destiny.core.chinese.eightwords.FlowDtoTransformer.toAffectingDtos
import destiny.core.chinese.eightwords.FlowDtoTransformer.toBranchCombinedDtos
import destiny.core.chinese.eightwords.FlowDtoTransformer.toBranchOppositionDtos
import destiny.core.chinese.eightwords.FlowDtoTransformer.toStemCombinedDtos
import destiny.core.chinese.eightwords.FlowDtoTransformer.toToFlowTrilogyDtos
import destiny.core.chinese.eightwords.FlowDtoTransformer.toTrilogyToFlowDtos
import destiny.core.electional.Dtos
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals

class FlowDtoTransformerTest {

  /** 大運測試 */
  @Nested
  inner class FlowLargeTest {

    @Nested
    inner class 大運天干合住本命天干 {

      @Test
      fun single() {
        val ew = EightWords(丙子, 乙未, 乙未, 己卯)
        val dtos = with(FlowLargePatterns.stemCombined) {
          ew.getPatterns(甲辰)
        }.map { it as FlowPattern.StemCombined }
          .toStemCombinedDtos()

        val expected = setOf(
          Dtos.EwEvent.EwFlow.StemCombinedDto(
            "本命時干(己) 被大運(甲)合住，甲己合化土",
            Dtos.NatalStems(setOf(HOUR), 己),
            Dtos.EwEvent.EwFlow.FlowStems(setOf(FlowScale.LARGE), 甲),
            FiveElement.土
          )
        )
        assertEquals(expected, dtos)
      }

      @Test
      fun multiple() {
        val ew = EightWords(丙子, 乙未, 己巳, 己巳)
        val dtos = with(FlowLargePatterns.stemCombined) {
          ew.getPatterns(甲辰)
        }.map { it as FlowPattern.StemCombined }
          .toStemCombinedDtos()

        val expected = setOf(
          Dtos.EwEvent.EwFlow.StemCombinedDto(
            "本命日干、時干(均為 己) 被大運(甲)合住，甲己合化土",
            Dtos.NatalStems(setOf(DAY, HOUR), 己),
            Dtos.EwEvent.EwFlow.FlowStems(setOf(FlowScale.LARGE), 甲),
            FiveElement.土
          )
        )
        assertEquals(expected, dtos)
      }
    }

    @Nested
    inner class 大運與本命地支相合 {

      @Test
      fun single() {
        val ew = EightWords(丙子, 乙未, 乙未, 己卯)
        val dtos = with(FlowLargePatterns.branchCombined) {
          ew.getPatterns(甲戌)
        }.map { it as FlowPattern.BranchCombined }
          .toBranchCombinedDtos()

        val expected = setOf(
          Dtos.EwEvent.EwFlow.BranchCombinedDto(
            "大運地支(戌) 合住 本命時支(卯)",
            Dtos.NatalBranches(setOf(HOUR), 卯),
            Dtos.EwEvent.EwFlow.FlowBranches(setOf(FlowScale.LARGE), 戌),
          )
        )
        assertEquals(expected, dtos)
      }

      @Test
      fun multiple() {
        val ew = EightWords(丙子, 乙未, 乙未, 癸未)
        val dtos = with(FlowLargePatterns.branchCombined) {
          ew.getPatterns(庚午)
        }.map { it as FlowPattern.BranchCombined }
          .toBranchCombinedDtos()

        val expected = setOf(
          Dtos.EwEvent.EwFlow.BranchCombinedDto(
            "大運地支(午) 合住 本命月支、日支、時支(均為 未)",
            Dtos.NatalBranches(setOf(MONTH, DAY, HOUR), 未),
            Dtos.EwEvent.EwFlow.FlowBranches(setOf(FlowScale.LARGE), 午),
          )
        )
        assertEquals(expected, dtos)
      }
    }

    @Nested
    inner class 本命已經三合取二_再拱大運 {

      @Test
      fun single() {
        val ew = EightWords(丙子, 乙未, 乙丑, 甲申)
        val dtos = with(trilogyToFlow) {
          ew.getPatterns(甲辰).toSet()
        }.map { it as FlowPattern.TrilogyToFlow }
          .toTrilogyToFlowDtos()

        val expected = setOf(
          Dtos.EwEvent.EwFlow.TrilogyToFlowDto(
            "大運(辰)與本命年柱(子)、時柱(申)三合水局",
            setOf(
              Dtos.NatalBranches(setOf(YEAR), 子),
              Dtos.NatalBranches(setOf(HOUR), 申)
            ),
            Dtos.EwEvent.EwFlow.FlowBranches(setOf(FlowScale.LARGE), 辰)
          )
        )
        assertEquals(expected, dtos)
      }

      @Test
      fun multiple() {
        val ew = EightWords(丙子, 乙未, 乙丑, 甲申)
        val dtos = with(trilogyToFlow) {
          ew.getPatterns(戊辰).toSet()
        }.map { it as FlowPattern.TrilogyToFlow }
          .toTrilogyToFlowDtos()

        val expected = setOf(
          Dtos.EwEvent.EwFlow.TrilogyToFlowDto(
            "大運(辰)與本命年柱(子)、時柱(申)三合水局",
            setOf(
              Dtos.NatalBranches(setOf(YEAR), 子),
              Dtos.NatalBranches(setOf(HOUR), 申)
            ),
            Dtos.EwEvent.EwFlow.FlowBranches(setOf(FlowScale.LARGE), 辰)
          )
        )
        assertEquals(expected, dtos)
      }

    }

    @Nested
    inner class 大運地支正沖本命 {

      @Test
      fun single() {
        val ew = EightWords(丙子, 乙未, 乙未, 己卯)
        val dtos = with(FlowLargePatterns.branchOpposition) {
          ew.getPatterns(癸酉).toSet()
        }.map { it as FlowPattern.BranchOpposition }
          .toBranchOppositionDtos()

        val expected = setOf(
          Dtos.EwEvent.EwFlow.BranchOppositionDto(
            "大運地支(酉) 正沖 本命時支(卯)",
            Dtos.NatalBranches(setOf(HOUR), 卯),
            Dtos.EwEvent.EwFlow.FlowBranches(setOf(FlowScale.LARGE), 酉)
          )
        )
        assertEquals(expected, dtos)
      }

      @Test
      fun multiple() {
        val ew = EightWords(丙子, 乙未, 乙未, 己卯)
        val dtos = with(FlowLargePatterns.branchOpposition) {
          ew.getPatterns(辛丑).toSet()
        }.map { it as FlowPattern.BranchOpposition }
          .toBranchOppositionDtos()

        val expected = setOf(
          Dtos.EwEvent.EwFlow.BranchOppositionDto(
            "大運地支(丑) 正沖 本命月支、日支(均為 未)",
            Dtos.NatalBranches(setOf(MONTH, DAY), 未),
            Dtos.EwEvent.EwFlow.FlowBranches(setOf(FlowScale.LARGE), 丑)
          )
        )
        assertEquals(expected, dtos)
      }

    }
  }

  /** 大運、流年測試 */
  @Nested
  inner class FlowLargeYearTest {

    @Test
    fun 大運流年同時影響本命天干() {
      val ew = EightWords(丙子, 乙未, 丙寅, 壬辰)
      val dtos = with(FlowLargeYearPatterns.affecting) {
        ew.getPatterns(甲辰, 甲戌).toSet()
      }.map { it as FlowPattern.Affecting }
        .toAffectingDtos()

      val expected = setOf(
        Dtos.EwEvent.EwFlow.AffectingDto(
          "本命年干、日干(均為 丙) 得到 大運、流年 五行 所生",
          Dtos.NatalStems(setOf(YEAR, DAY), 丙),
          Reacting.PRODUCED,
          setOf(FlowScale.LARGE, FlowScale.YEAR)
        ),
        Dtos.EwEvent.EwFlow.AffectingDto(
          "本命月干(乙) 與 大運、流年五行相同",
          Dtos.NatalStems(setOf(MONTH), 乙),
          Reacting.SAME,
          setOf(FlowScale.LARGE, FlowScale.YEAR)
        ),
        Dtos.EwEvent.EwFlow.AffectingDto(
          "本命時干(壬) 生/洩 出大運、流年",
          Dtos.NatalStems(setOf(HOUR), 壬),
          Reacting.PRODUCING,
          setOf(FlowScale.LARGE, FlowScale.YEAR)
        )
      )
      assertEquals(expected, dtos)
    }

    @Nested
    inner class 大運流年合住本命天干 {

      @Test
      fun single() {
        val ew = EightWords(丙子, 乙未, 乙未, 己卯)
        val dtos = with(FlowLargeYearPatterns.stemCombined) {
          ew.getPatterns(甲辰, 甲戌).toSet()
        }.map { it as FlowPattern.StemCombined }
          .toStemCombinedDtos()

        val expected = setOf(
          Dtos.EwEvent.EwFlow.StemCombinedDto(
            "本命時干(己) 被大運、流年(均為 甲)合住，甲己合化土",
            Dtos.NatalStems(setOf(HOUR), 己),
            Dtos.EwEvent.EwFlow.FlowStems(setOf(FlowScale.LARGE, FlowScale.YEAR), 甲),
            FiveElement.土
          )
        )
        assertEquals(expected, dtos)
      }

      @Test
      fun multiple() {
        val ew = EightWords(丙子, 乙未, 己巳, 己巳)
        val dtos = with(FlowLargeYearPatterns.stemCombined) {
          ew.getPatterns(甲辰, 甲戌).toSet()
        }.map { it as FlowPattern.StemCombined }
          .toStemCombinedDtos()

        val expected = setOf(
          Dtos.EwEvent.EwFlow.StemCombinedDto(
            "本命日干、時干(均為 己) 被大運、流年(均為 甲)合住，甲己合化土",
            Dtos.NatalStems(setOf(DAY, HOUR), 己),
            Dtos.EwEvent.EwFlow.FlowStems(setOf(FlowScale.LARGE, FlowScale.YEAR), 甲),
            FiveElement.土
          )
        )
        assertEquals(expected, dtos)
      }
    }

    @Nested
    inner class 大運流年合住本命地支 {

      @Test
      fun single() {
        val ew = EightWords(丙子, 乙未, 乙未, 己卯)
        val dtos = with(FlowLargeYearPatterns.branchCombined) {
          ew.getPatterns(甲辰, 甲戌).toSet()
        }.map { it as FlowPattern.BranchCombined }
          .toBranchCombinedDtos()

        val expected = setOf(
          Dtos.EwEvent.EwFlow.BranchCombinedDto(
            "流年地支(戌) 合住 本命時支(卯)",
            Dtos.NatalBranches(setOf(HOUR), 卯),
            Dtos.EwEvent.EwFlow.FlowBranches(setOf(FlowScale.YEAR), 戌),
          )
        )
        assertEquals(expected, dtos)
      }

      @Test
      fun multiple1() {
        val ew = EightWords(丙子, 乙未, 乙未, 乙酉)
        val dtos = with(FlowLargeYearPatterns.branchCombined) {
          ew.getPatterns(甲辰, 庚午).toSet()
        }.map { it as FlowPattern.BranchCombined }
          .toBranchCombinedDtos()

        val expected = setOf(
          Dtos.EwEvent.EwFlow.BranchCombinedDto(
            "大運地支(辰) 合住 本命時支(酉)",
            Dtos.NatalBranches(setOf(HOUR), 酉),
            Dtos.EwEvent.EwFlow.FlowBranches(setOf(FlowScale.LARGE), 辰),
          ),
          Dtos.EwEvent.EwFlow.BranchCombinedDto(
            "流年地支(午) 合住 本命月支、日支(均為 未)",
            Dtos.NatalBranches(setOf(MONTH, DAY), 未),
            Dtos.EwEvent.EwFlow.FlowBranches(setOf(FlowScale.YEAR), 午),
          )
        )
        assertEquals(expected, dtos)
      }

      @Test
      fun multiple2() {
        val ew = EightWords(丙子, 乙未, 乙未, 乙酉)
        val dtos = with(FlowLargeYearPatterns.branchCombined) {
          ew.getPatterns(丙午, 丙午).toSet()
        }.map { it as FlowPattern.BranchCombined }
          .toBranchCombinedDtos()

        val expected = setOf(
          Dtos.EwEvent.EwFlow.BranchCombinedDto(
            "大運、流年地支(均為 午) 合住 本命月支、日支(均為 未)",
            Dtos.NatalBranches(setOf(MONTH, DAY), 未),
            Dtos.EwEvent.EwFlow.FlowBranches(setOf(FlowScale.LARGE, FlowScale.YEAR), 午),
          )
        )
        assertEquals(expected, dtos)
      }
    }

    @Nested
    inner class 本命已經三合取二_再拱大運或流年 {

      @Test
      fun single() {
        val ew = EightWords(丙子, 乙未, 乙丑, 甲申)
        val dtos = with(FlowLargeYearPatterns.trilogyToFlow) {
          ew.getPatterns(甲辰, 丙子).toSet()
        }.map { it as FlowPattern.TrilogyToFlow }
          .toTrilogyToFlowDtos()

        val expected = setOf(
          Dtos.EwEvent.EwFlow.TrilogyToFlowDto(
            "大運(辰)與本命年柱(子)、時柱(申)三合水局",
            setOf(
              Dtos.NatalBranches(setOf(YEAR), 子),
              Dtos.NatalBranches(setOf(HOUR), 申)
            ),
            Dtos.EwEvent.EwFlow.FlowBranches(setOf(FlowScale.LARGE), 辰)
          )
        )
        assertEquals(expected, dtos)
      }

      @Test
      fun multiple() {
        val ew = EightWords(丙子, 乙未, 乙丑, 甲申)
        val dtos = with(FlowLargeYearPatterns.trilogyToFlow) {
          ew.getPatterns(甲辰, 戊辰).toSet()
        }.map { it as FlowPattern.TrilogyToFlow }
          .toTrilogyToFlowDtos()

        val expected = setOf(
          Dtos.EwEvent.EwFlow.TrilogyToFlowDto(
            "大運、流年(均為 辰)與本命年柱(子)、時柱(申)三合水局",
            setOf(
              Dtos.NatalBranches(setOf(YEAR), 子),
              Dtos.NatalBranches(setOf(HOUR), 申)
            ),
            Dtos.EwEvent.EwFlow.FlowBranches(setOf(FlowScale.LARGE, FlowScale.YEAR), 辰)
          )
        )
        assertEquals(expected, dtos)
      }
    }

    @Nested
    inner class 大運流年三合_再拱本命地支 {

      @Test
      fun single() {
        val ew = EightWords(丙子, 乙未, 乙丑, 甲申)
        val dtos = with(toFlowTrilogy) {
          ew.getPatterns(甲辰, 丙子).toSet()
        }.map { it as FlowPattern.ToFlowTrilogy }
          .toToFlowTrilogyDtos()

        val expected = setOf(
          Dtos.EwEvent.EwFlow.ToFlowTrilogyDto(
            "大運(辰)、流年(子) 與本命時支(申) 三合 水局",
            Dtos.NatalBranches(setOf(HOUR), 申),
            setOf(
              Dtos.EwEvent.EwFlow.FlowBranches(setOf(FlowScale.LARGE), 辰),
              Dtos.EwEvent.EwFlow.FlowBranches(setOf(FlowScale.YEAR), 子)
            )
          )
        )
        assertEquals(expected, dtos)
      }

      @Test
      fun multiple() {
        val ew = EightWords(丙子, 乙未, 乙丑, 丙子)
        val dtos = with(toFlowTrilogy) {
          ew.getPatterns(甲辰, 壬申).toSet()
        }.map { it as FlowPattern.ToFlowTrilogy }
          .toToFlowTrilogyDtos()

        val expected = setOf(
          Dtos.EwEvent.EwFlow.ToFlowTrilogyDto(
            "大運(辰)、流年(申) 與本命年支、時支(均為子) 三合 水局",
            Dtos.NatalBranches(setOf(YEAR, HOUR), 子),
            setOf(
              Dtos.EwEvent.EwFlow.FlowBranches(setOf(FlowScale.LARGE), 辰),
              Dtos.EwEvent.EwFlow.FlowBranches(setOf(FlowScale.YEAR), 申)
            )
          )
        )
        assertEquals(expected, dtos)
      }
    }

    @Nested
    inner class 大運流年正沖本命地支 {

      @Test
      fun single() {
        val ew = EightWords(丙子, 乙未, 乙未, 己卯)
        val dtos = with(FlowLargeYearPatterns.branchOpposition) {
          ew.getPatterns(甲戌, 癸酉).toSet()
        }.map { it as FlowPattern.BranchOpposition }
          .toBranchOppositionDtos()

        val expected = setOf(
          Dtos.EwEvent.EwFlow.BranchOppositionDto(
            "流年地支(酉) 正沖 本命時支(卯)",
            Dtos.NatalBranches(setOf(HOUR), 卯),
            Dtos.EwEvent.EwFlow.FlowBranches(setOf(FlowScale.YEAR), 酉)
          )
        )
        assertEquals(expected, dtos)
      }

      @Test
      fun multiple() {
        val ew = EightWords(丙子, 乙未, 乙未, 己卯)
        val dtos = with(FlowLargeYearPatterns.branchOpposition) {
          ew.getPatterns(辛丑, 辛丑).toSet()
        }.map { it as FlowPattern.BranchOpposition }
          .toBranchOppositionDtos()

        val expected = setOf(
          Dtos.EwEvent.EwFlow.BranchOppositionDto(
            "大運、流年地支(均為 丑) 正沖 本命月支、日支(均為 未)",
            Dtos.NatalBranches(setOf(MONTH, DAY), 未),
            Dtos.EwEvent.EwFlow.FlowBranches(setOf(FlowScale.LARGE, FlowScale.YEAR), 丑)
          )
        )
        assertEquals(expected, dtos)
      }
    }
  }

  /** 流年、流月測試 */
  @Nested
  inner class FlowYearMonthTest {

    @Test
    fun 流年流月同時影響本命天干() {
      val ew = EightWords(丙子, 乙未, 丙寅, 壬辰)
      val dtos = with(affecting) {
        ew.getPatterns(甲辰, 甲戌)
      }.map { it as FlowPattern.Affecting }
        .toAffectingDtos()

      val expected = setOf(
        Dtos.EwEvent.EwFlow.AffectingDto(
          "本命年干、日干(均為 丙) 得到 流年、流月 五行 所生",
          Dtos.NatalStems(setOf(YEAR, DAY), 丙),
          Reacting.PRODUCED,
          setOf(FlowScale.YEAR, FlowScale.MONTH)
        ),
        Dtos.EwEvent.EwFlow.AffectingDto(
          "本命月干(乙) 與 流年、流月五行相同",
          Dtos.NatalStems(setOf(MONTH), 乙),
          Reacting.SAME,
          setOf(FlowScale.YEAR, FlowScale.MONTH)
        ),
        Dtos.EwEvent.EwFlow.AffectingDto(
          "本命時干(壬) 生/洩 出流年、流月",
          Dtos.NatalStems(setOf(HOUR), 壬),
          Reacting.PRODUCING,
          setOf(FlowScale.YEAR, FlowScale.MONTH)
        )
      )
      assertEquals(expected, dtos)
    }

    @Nested
    inner class 流年流月合住本命天干 {

      @Test
      fun single() {
        val ew = EightWords(丙子, 乙未, 乙未, 己卯)
        val dtos = with(FlowYearMonthPatterns.stemCombined) {
          ew.getPatterns(甲辰, 甲戌).toSet()
        }.map { it as FlowPattern.StemCombined }
          .toStemCombinedDtos()

        val expected = setOf(
          Dtos.EwEvent.EwFlow.StemCombinedDto(
            "本命時干(己) 被流年、流月(均為 甲)合住，甲己合化土",
            Dtos.NatalStems(setOf(HOUR), 己),
            Dtos.EwEvent.EwFlow.FlowStems(setOf(FlowScale.YEAR, FlowScale.MONTH), 甲),
            FiveElement.土
          )
        )
        assertEquals(expected, dtos)
      }

      @Test
      fun multiple() {
        val ew = EightWords(丙子, 乙未, 己巳, 己巳)
        val dtos = with(FlowYearMonthPatterns.stemCombined) {
          ew.getPatterns(甲辰, 甲戌).toSet()
        }.map { it as FlowPattern.StemCombined }
          .toStemCombinedDtos()

        val expected = setOf(
          Dtos.EwEvent.EwFlow.StemCombinedDto(
            "本命日干、時干(均為 己) 被流年、流月(均為 甲)合住，甲己合化土",
            Dtos.NatalStems(setOf(DAY, HOUR), 己),
            Dtos.EwEvent.EwFlow.FlowStems(setOf(FlowScale.YEAR, FlowScale.MONTH), 甲),
            FiveElement.土
          )
        )
        assertEquals(expected, dtos)
      }
    }

    @Nested
    inner class 流年流月合住本命地支 {

      @Test
      fun single() {
        val ew = EightWords(丙子, 乙未, 乙未, 己卯)
        val dtos = with(FlowYearMonthPatterns.branchCombined) {
          ew.getPatterns(甲辰, 甲戌).toSet()
        }.map { it as FlowPattern.BranchCombined }
          .toBranchCombinedDtos()

        val expected = setOf(
          Dtos.EwEvent.EwFlow.BranchCombinedDto(
            "流月地支(戌) 合住 本命時支(卯)",
            Dtos.NatalBranches(setOf(HOUR), 卯),
            Dtos.EwEvent.EwFlow.FlowBranches(setOf(FlowScale.MONTH), 戌),
          )
        )
        assertEquals(expected, dtos)
      }

      @Test
      fun multiple1() {
        val ew = EightWords(丙子, 乙未, 乙未, 乙酉)
        val dtos = with(FlowYearMonthPatterns.branchCombined) {
          ew.getPatterns(甲辰, 庚午).toSet()
        }.map { it as FlowPattern.BranchCombined }
          .toBranchCombinedDtos()

        val expected = setOf(
          Dtos.EwEvent.EwFlow.BranchCombinedDto(
            "流月地支(午) 合住 本命月支、日支(均為 未)",
            Dtos.NatalBranches(setOf(MONTH, DAY), 未),
            Dtos.EwEvent.EwFlow.FlowBranches(setOf(FlowScale.MONTH), 午),
          ),
          Dtos.EwEvent.EwFlow.BranchCombinedDto(
            "流年地支(辰) 合住 本命時支(酉)",
            Dtos.NatalBranches(setOf(HOUR), 酉),
            Dtos.EwEvent.EwFlow.FlowBranches(setOf(FlowScale.YEAR), 辰),
          )
        )
        assertEquals(expected, dtos)
      }

      @Test
      fun multiple2() {
        val ew = EightWords(丙子, 乙未, 乙未, 乙酉)
        val dtos = with(FlowYearMonthPatterns.branchCombined) {
          ew.getPatterns(丙午, 丙午).toSet()
        }.map { it as FlowPattern.BranchCombined }
          .toBranchCombinedDtos()

        val expected = setOf(
          Dtos.EwEvent.EwFlow.BranchCombinedDto(
            "流年、流月地支(均為 午) 合住 本命月支、日支(均為 未)",
            Dtos.NatalBranches(setOf(MONTH, DAY), 未),
            Dtos.EwEvent.EwFlow.FlowBranches(setOf(FlowScale.YEAR, FlowScale.MONTH), 午),
          )
        )
        assertEquals(expected, dtos)
      }

    }

    @Nested
    inner class 本命已經三合取二_再拱流年或流月 {
      @Test
      fun single() {
        val ew = EightWords(丙子, 乙未, 乙丑, 甲申)
        val dtos = with(FlowYearMonthPatterns.trilogyToFlow) {
          ew.getPatterns(甲辰, 丙子).toSet()
        }.map { it as FlowPattern.TrilogyToFlow }
          .toTrilogyToFlowDtos()

        val expected = setOf(
          Dtos.EwEvent.EwFlow.TrilogyToFlowDto(
            "流年(辰)與本命年柱(子)、時柱(申)三合水局",
            setOf(
              Dtos.NatalBranches(setOf(YEAR), 子),
              Dtos.NatalBranches(setOf(HOUR), 申)
            ),
            Dtos.EwEvent.EwFlow.FlowBranches(setOf(FlowScale.YEAR), 辰)
          )
        )
        assertEquals(expected, dtos)
      }

      @Test
      fun multiple() {
        val ew = EightWords(丙子, 乙未, 乙丑, 甲申)
        val dtos = with(FlowYearMonthPatterns.trilogyToFlow) {
          ew.getPatterns(甲辰, 戊辰).toSet()
        }.map { it as FlowPattern.TrilogyToFlow }
          .toTrilogyToFlowDtos()

        val expected = setOf(
          Dtos.EwEvent.EwFlow.TrilogyToFlowDto(
            "流年、流月(均為 辰)與本命年柱(子)、時柱(申)三合水局",
            setOf(
              Dtos.NatalBranches(setOf(YEAR), 子),
              Dtos.NatalBranches(setOf(HOUR), 申)
            ),
            Dtos.EwEvent.EwFlow.FlowBranches(setOf(FlowScale.YEAR, FlowScale.MONTH), 辰)
          )
        )
        assertEquals(expected, dtos)
      }

    }

    @Nested
    inner class 流年流月三合_再拱本命地支 {

      @Test
      fun single() {
        val ew = EightWords(丙子, 乙未, 乙丑, 甲申)
        val dtos = with(FlowYearMonthPatterns.toFlowTrilogy) {
          ew.getPatterns(甲辰, 丙子).toSet()
        }.map { it as FlowPattern.ToFlowTrilogy }
          .toToFlowTrilogyDtos()

        val expected = setOf(
          Dtos.EwEvent.EwFlow.ToFlowTrilogyDto(
            "流年(辰)、流月(子) 與本命時支(申) 三合 水局",
            Dtos.NatalBranches(setOf(HOUR), 申),
            setOf(
              Dtos.EwEvent.EwFlow.FlowBranches(setOf(FlowScale.YEAR), 辰),
              Dtos.EwEvent.EwFlow.FlowBranches(setOf(FlowScale.MONTH), 子)
            )
          )
        )
        assertEquals(expected, dtos)
      }

      @Test
      fun multiple() {
        val ew = EightWords(丙子, 乙未, 乙丑, 丙子)
        val dtos = with(FlowYearMonthPatterns.toFlowTrilogy) {
          ew.getPatterns(甲辰, 壬申).toSet()
        }.map { it as FlowPattern.ToFlowTrilogy }
          .toToFlowTrilogyDtos()

        val expected = setOf(
          Dtos.EwEvent.EwFlow.ToFlowTrilogyDto(
            "流年(辰)、流月(申) 與本命年支、時支(均為子) 三合 水局",
            Dtos.NatalBranches(setOf(YEAR, HOUR), 子),
            setOf(
              Dtos.EwEvent.EwFlow.FlowBranches(setOf(FlowScale.YEAR), 辰),
              Dtos.EwEvent.EwFlow.FlowBranches(setOf(FlowScale.MONTH), 申)
            )
          )
        )
        assertEquals(expected, dtos)
      }

    }

    @Nested
    inner class 流年流月正沖本命地支 {

      @Test
      fun single() {
        val ew = EightWords(丙子, 乙未, 乙未, 己卯)
        val dtos = with(FlowYearMonthPatterns.branchOpposition) {
          ew.getPatterns(甲辰, 癸酉).toSet()
        }.map { it as FlowPattern.BranchOpposition }
          .toBranchOppositionDtos()

        val expected = setOf(
          Dtos.EwEvent.EwFlow.BranchOppositionDto(
            "流月地支(酉) 正沖 本命時支(卯)",
            Dtos.NatalBranches(setOf(HOUR), 卯),
            Dtos.EwEvent.EwFlow.FlowBranches(setOf(FlowScale.MONTH), 酉)
          )
        )
        assertEquals(expected, dtos)
      }

      @Test
      fun multiple() {
        val ew = EightWords(丙子, 乙未, 乙未, 己卯)
        val dtos = with(FlowYearMonthPatterns.branchOpposition) {
          ew.getPatterns(辛丑, 辛丑).toSet()
        }.map { it as FlowPattern.BranchOpposition }
          .toBranchOppositionDtos()

        val expected = setOf(
          Dtos.EwEvent.EwFlow.BranchOppositionDto(
            "流年、流月地支(均為 丑) 正沖 本命月支、日支(均為 未)",
            Dtos.NatalBranches(setOf(MONTH, DAY), 未),
            Dtos.EwEvent.EwFlow.FlowBranches(setOf(FlowScale.YEAR, FlowScale.MONTH), 丑)
          )
        )
        assertEquals(expected, dtos)
      }
    }

  }
}
