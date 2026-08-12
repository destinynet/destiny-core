/**
 * Created by smallufo on 2026-08-13.
 */
package destiny.core.chinese.eightwords

import destiny.core.ChartDensity
import destiny.core.Gender
import destiny.core.astrology.*
import destiny.core.calendar.*
import destiny.core.calendar.chinese.ChineseDate
import destiny.core.calendar.eightwords.EightWords
import destiny.core.calendar.eightwords.IEightWords
import destiny.core.chinese.StemBranch
import destiny.core.chinese.eightwords.hazards.HazardItem
import destiny.core.identityFieldsIn
import java.time.LocalDateTime
import java.time.chrono.ChronoLocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * 固定樣本取自 wicket 既有畫面：男、丙午年 丙申月 己未日 乙丑時（日主 己）。
 * 十神與藏干十神的期望值即該畫面上逐格對照過的內容。
 */
class EwChartDtoTest {

  @Test
  fun pillars_areOrderedYearToHour() {
    val dto = fakeModel().toEwChartDto(ChartDensity.FULL)

    assertEquals(listOf("YEAR", "MONTH", "DAY", "HOUR"), dto.pillars.map { it.scale })
    assertEquals(listOf("丙", "丙", "己", "乙"), dto.pillars.map { it.stem })
    assertEquals(listOf("午", "申", "未", "丑"), dto.pillars.map { it.branch })
  }

  @Test
  fun pillars_carryStemReactionWithAbbreviation() {
    val dto = fakeModel().toEwChartDto(ChartDensity.FULL)

    val year = dto.pillars.single { it.scale == "YEAR" }
    assertEquals("正印", year.stemReaction)
    assertEquals("印", year.stemReactionAbbr)

    val hour = dto.pillars.single { it.scale == "HOUR" }
    assertEquals("七殺", hour.stemReaction)
    assertEquals("殺", hour.stemReactionAbbr)
  }

  /** 日柱天干就是日主，對自己談十神沒有意義；那一格由 renderer 印「日主」或性別 */
  @Test
  fun dayPillar_carriesNoStemReaction() {
    val day = fakeModel().toEwChartDto(ChartDensity.FULL).pillars.single { it.scale == "DAY" }

    assertNull(day.stemReaction)
    assertNull(day.stemReactionAbbr)
  }

  @Test
  fun pillars_carryHiddenStemsWithReactions() {
    val dto = fakeModel().toEwChartDto(ChartDensity.FULL)

    assertEquals(
      listOf("庚・傷官", "壬・正財", "戊・劫財"),
      dto.pillars.single { it.scale == "MONTH" }.hiddenStems.map { "${it.stem}・${it.reaction}" }
    )
    assertEquals(
      listOf("傷", "財", "劫"),
      dto.pillars.single { it.scale == "MONTH" }.hiddenStems.map { it.abbr }
    )
  }

  /** COMPACT 只留干支本體 —— 十神是關係疊層，四張並排時不畫 */
  @Test
  fun compactDensity_keepsOnlyStemsAndBranches() {
    val dto = fakeModel().toEwChartDto(ChartDensity.COMPACT)

    assertEquals(4, dto.pillars.size)
    assertTrue(dto.pillars.all { it.stemReaction == null && it.hiddenStems.isEmpty() && it.naYin == null })
    assertTrue(dto.fortunes.isEmpty())
    assertNull(dto.solarTerms)
    assertNull(dto.meta.score)
    assertNull(dto.meta.mingStemBranch)
    assertEquals("M", dto.meta.gender)
  }

  /** 納音與空亡屬雜項，只在 ALL 出現（wicket 的納音本來就是可關的開關） */
  @Test
  fun naYinAndEmpties_onlyInAllDensity() {
    val model = fakeModel()

    val full = model.toEwChartDto(ChartDensity.FULL)
    assertTrue(full.pillars.all { it.naYin == null })
    assertTrue(full.meta.dayEmpties.isEmpty())

    val all = model.toEwChartDto(ChartDensity.ALL)
    assertEquals("天河水", all.pillars.single { it.scale == "YEAR" }.naYin)
    assertEquals(listOf("子", "丑"), all.meta.dayEmpties)
  }

  @Test
  fun fortunes_carryAgesAndStemReaction() {
    val dto = fakeModel().toEwChartDto(ChartDensity.FULL)

    assertEquals(2, dto.fortunes.size)
    val first = dto.fortunes.first()
    assertEquals("丁", first.stem)
    assertEquals("酉", first.branch)
    assertEquals(10, first.fromAge)
    assertEquals(19, first.toAge)
    assertEquals("偏印", first.stemReaction)
    // 大運的藏干十神屬雜項
    assertTrue(first.hiddenStems.isEmpty())
    assertEquals(
      listOf("辛・食神"),
      fakeModel().toEwChartDto(ChartDensity.ALL).fortunes.first().hiddenStems.map { "${it.stem}・${it.reaction}" }
    )
  }

  @Test
  fun solarTerms_carryNeighboursAndProgress() {
    val st = fakeModel().toEwChartDto(ChartDensity.FULL).solarTerms!!

    assertEquals("立秋", st.prevMajor)
    assertEquals("申", st.prevMajorBranch)
    assertEquals("處暑", st.middle)
    assertEquals("處女", st.middleSign)
    assertEquals("白露", st.nextMajor)
    assertEquals("酉", st.nextMajorBranch)
    assertEquals(5, st.daysFromPrev)
    assertEquals(0.17, st.ratio, 0.01)
  }

  @Test
  fun meta_carriesGenderMingHouseAndScore() {
    val meta = fakeModel().toEwChartDto(ChartDensity.FULL).meta

    assertEquals("M", meta.gender)
    assertEquals("丙申", meta.mingStemBranch)
    assertEquals("雙子", meta.mingSign)
    assertEquals(5.0, meta.score)
  }

  /**
   * 古書命例（三命通會、滴天髓…）只有四柱與性別，沒有時刻，
   * 因此排不出大運、節氣位置、命宮，八分法也算不出來（月支分數取決於節氣深淺）。
   */
  @Test
  fun pillarsOnlyChart_carriesNoTimeDerivedData() {
    val dto = EightWords("丙午", "丙申", "己未", "乙丑").toEwChartDto(Gender.M, ChartDensity.ALL)

    assertTrue(dto.meta.pillarsOnly)
    assertTrue(dto.fortunes.isEmpty())
    assertNull(dto.solarTerms)
    assertNull(dto.meta.mingStemBranch)
    assertNull(dto.meta.mingSign)
    assertNull(dto.meta.score)
    assertEquals("M", dto.meta.gender)
  }

  /** 有時刻的盤要標成 false，renderer 才知道能不能給密度切換 */
  @Test
  fun timedChart_isNotFlaggedPillarsOnly() {
    assertFalse(fakeModel().toEwChartDto(ChartDensity.FULL).meta.pillarsOnly)
  }

  /**
   * 純四柱仍有三級可分 —— 十神、藏干、納音、空亡全都只需要八個字，
   * 故密度切換依然有意義，不必因缺時刻而鎖死。
   */
  @Test
  fun pillarsOnlyChart_stillHonoursDensityLadder() {
    val ew = EightWords("丙午", "丙申", "己未", "乙丑")

    val compact = ew.toEwChartDto(Gender.M, ChartDensity.COMPACT)
    assertTrue(compact.pillars.all { it.stemReaction == null && it.hiddenStems.isEmpty() })

    val full = ew.toEwChartDto(Gender.M, ChartDensity.FULL)
    assertEquals("正印", full.pillars.single { it.scale == "YEAR" }.stemReaction)
    assertEquals(3, full.pillars.single { it.scale == "MONTH" }.hiddenStems.size)
    assertTrue(full.pillars.all { it.naYin == null })

    val all = ew.toEwChartDto(Gender.M, ChartDensity.ALL)
    assertEquals("天河水", all.pillars.single { it.scale == "YEAR" }.naYin)
    assertEquals(listOf("子", "丑"), all.meta.dayEmpties)
  }

  @Test
  fun ewChartDto_declaresNoIdentityField() {
    assertEquals(emptyList(), identityFieldsIn(EwChartDto.serializer().descriptor))
  }

  // ---- fixtures ----

  /** 立秋起算第 5.2 天，整個節氣長 30.4 天 */
  private val prevMajorBegin = GmtJulDay(2439300.0)
  private val viewJulDay = GmtJulDay(2439305.2)
  private val nextMajorBegin = GmtJulDay(2439330.4)

  private fun fakeModel() = FakeEwModel(
    eightWords = EightWords("丙午", "丙申", "己未", "乙丑"),
    fortuneDataLarges = listOf(
      fortuneOf("丁酉", 10, 19),
      fortuneOf("戊戌", 20, 29),
    ),
    risingStemBranch = StemBranch.丙申,
    solarTermsTimePos = SolarTermsTimePos(
      gmtJulDay = viewJulDay,
      prevMajor = SolarTermsEvent(prevMajorBegin, SolarTerms.立秋),
      middle = SolarTermsEvent(GmtJulDay(2439315.0), SolarTerms.處暑),
      nextMajor = SolarTermsEvent(nextMajorBegin, SolarTerms.白露),
    ),
    prevSolarSign = ZodiacSign.LEO to prevMajorBegin,
    nextSolarSign = ZodiacSign.VIRGO to GmtJulDay(2439315.0),
    score = 5.0,
  )

  /**
   * 起訖時刻與 [FortuneData.startFortuneAgeNotes] 刻意填入可辨識的值 ——
   * 前者是精確時刻、後者的內容就是「西元 2035」這類年份，兩者都不得進入 DTO。
   */
  private fun fortuneOf(stemBranch: String, from: Int, to: Int) = FortuneData(
    stemBranch = StemBranch[stemBranch],
    startFortuneGmtJulDay = GmtJulDay(2442000.0),
    endFortuneGmtJulDay = GmtJulDay(2445650.0),
    startFortuneAge = from,
    endFortuneAge = to,
    startFortuneAgeNotes = listOf("西元2035年"),
    endFortuneAgeNotes = listOf("西元2044年"),
  )
}

/**
 * 最小假盤 —— 只填 mapper 讀得到的欄位。
 * 刻意帶入 [name]、[place]、[chineseDate]，好讓「盤面 DTO 不得洩漏身分」有東西可測。
 */
private class FakeEwModel(
  override val eightWords: IEightWords,
  override val fortuneDataLarges: List<FortuneData>,
  override val risingStemBranch: StemBranch,
  override val solarTermsTimePos: SolarTermsTimePos,
  override val prevSolarSign: Pair<ZodiacSign, GmtJulDay>,
  override val nextSolarSign: Pair<ZodiacSign, GmtJulDay>,
  override val score: Double,
) : IPersonContextModel {
  override val gender: Gender = Gender.M
  override val name: String? = "王小明"
  override val place: String? = "台北市"
  override val time: ChronoLocalDateTime<*> = LocalDateTime.of(1966, 7, 17, 1, 3)
  override val location: ILocation = Location.of(25.04, 121.51, "Asia/Taipei")
  override val chineseDate: ChineseDate = ChineseDate(78, StemBranch.丙午, 7, false, 1)
  override val dst: Boolean = false
  override val gmtMinuteOffset: Int = 480
  override val starPosMap: Map<AstroPoint, PositionWithBranch> = emptyMap()
  override val houseMap: Map<Int, ZodiacDegree> = emptyMap()
  override val rsmiMap: Map<TransPoint, ZodiacDegree> = emptyMap()
  override val aspectsDataSet: Set<IPointAspectPattern> = emptySet()
  override val fortuneDataSmalls: List<FortuneData> = emptyList()
  override val ageMap: Map<Int, Pair<GmtJulDay, GmtJulDay>> = emptyMap()
  override val childHazards: List<HazardItem> = emptyList()
}
