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
import destiny.core.chinese.Branch
import destiny.core.chinese.StemBranch
import destiny.core.chinese.eightwords.hazards.HazardItem
import destiny.core.identityFieldsIn
import destiny.tools.Lang
import destiny.tools.getTitle
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

  /** 流年年份配上歲數可推出出生年 —— 預設不帶 */
  @Test
  fun fortuneYears_absentByDefault() {
    val dto = modelWithAges().toEwChartDto(ChartDensity.ALL)
    assertTrue(dto.fortunes.isNotEmpty())
    assertTrue(dto.fortunes.all { it.years.isEmpty() })
  }

  /**
   * 每一歲止於立春；大運 1975-07 ～ 1985-07 涵蓋 10 歲（1975 立春起）到 20 歲（1985 立春起），
   * 交界的兩歲各自也屬於前後那步大運。
   */
  @Test
  fun fortuneYears_whenAskedFor_consecutiveYearsWithStemBranch() {
    val dto = modelWithAges().toEwChartDto(ChartDensity.FULL, withYears = true)

    val first = dto.fortunes.first()
    assertEquals((10..20).toList(), first.years.map { it.atAge })
    assertEquals((1975..1985).toList(), first.years.map { it.year })
    first.years.first().also {
      assertEquals("乙", it.stem)
      assertEquals("卯", it.branch)
      assertEquals("七殺", it.stemReaction)
      assertEquals("殺", it.stemReactionAbbr)
    }
    assertEquals("甲子", first.years.single { it.year == 1984 }.let { it.stem + it.branch })

    // 交界那一歲（20 歲）同時出現在下一步大運
    val second = dto.fortunes[1]
    assertEquals(20, second.years.first().atAge)
    assertEquals(1985, second.years.first().year)
  }

  /** 大運與流年的藏干：FULL 預設不帶，明示 fortuneHiddenStems 才帶（流年跟著同一個開關） */
  @Test
  fun fortuneHiddenStems_offByDefaultInFull_onWhenAskedFor() {
    val plain = modelWithAges().toEwChartDto(ChartDensity.FULL, withYears = true)
    assertTrue(plain.fortunes.all { f -> f.hiddenStems.isEmpty() && f.years.all { it.hiddenStems.isEmpty() } })

    val dto = modelWithAges().toEwChartDto(ChartDensity.FULL, withYears = true, fortuneHiddenStems = true)
    assertTrue(dto.fortunes.all { it.hiddenStems.isNotEmpty() })
    // 1975 乙卯：卯藏乙，日主見乙為七殺
    dto.fortunes.first().years.first().hiddenStems.also {
      assertEquals(listOf("乙"), it.map { h -> h.stem })
      assertEquals("七殺", it.first().reaction)
    }
  }

  /** 出生在立春前：第一歲止於當年立春，年柱屬前一年 */
  @Test
  fun fortuneYears_bornBeforeLiChun_firstAgeBelongsToPreviousYear() {
    val birth = jd(1966, 1, 20)
    val ages = mapOf(1 to (birth to jd(1966, 2, 4)), 2 to (jd(1966, 2, 4) to jd(1967, 2, 4)))
    val model = modelWithAges(ageMap = ages, larges = listOf(fortuneOf("丁酉", 1, 10, birth - 1, jd(1975, 7, 1))))
    val years = model.toEwChartDto(ChartDensity.FULL, withYears = true).fortunes.first().years
    assertEquals(listOf(1965, 1966), years.map { it.year })
    assertEquals(listOf("乙巳", "丙午"), years.map { it.stem + it.branch })
  }

  @Test
  fun altHourBranch_absentByDefault_andWhenSameAsChart() {
    assertNull(fakeModel().toEwChartDto(ChartDensity.FULL).meta.altHourBranch)
    // 本盤時支 丑
    assertNull(fakeModel().toEwChartDto(ChartDensity.FULL, altHourBranch = Branch.丑).meta.altHourBranch)
    assertEquals("子", fakeModel().toEwChartDto(ChartDensity.FULL, altHourBranch = Branch.子).meta.altHourBranch)
  }

  /** 沒有時辰的盤，「另一套時刻系統下的時辰」也無從談起 */
  @Test
  fun altHourBranch_droppedWithoutHour() {
    val dto = fakeModel().toEwChartDto(ChartDensity.FULL, altHourBranch = Branch.子)
    assertNull(dto.gatedBy(BirthDataGrain.DAY).meta.altHourBranch)
    assertEquals("子", dto.gatedBy(BirthDataGrain.MINUTE).meta.altHourBranch)
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

  /** 三個點刻意落在三個不同星座，對錯位（上升拿到太陽的星座…）才測得出來 */
  private fun modelWithSigns() = fakeModel().let { m ->
    FakeEwModel(
      m.eightWords, m.fortuneDataLarges, m.risingStemBranch, m.solarTermsTimePos, m.prevSolarSign, m.nextSolarSign, m.score,
      starPosMap = mapOf(
        Planet.SUN to PositionWithBranch(Pos(45.0, 0.0), StemBranch.甲子),    // 金牛 15°
        Planet.MOON to PositionWithBranch(Pos(200.0, 0.0), StemBranch.甲子),  // 天秤 20°
      ),
      rsmiMap = mapOf(TransPoint.RISING to ZodiacDegree.of(ZodiacSign.CANCER, 10.0)),
    )
  }

  @Test
  fun signs_absentByDefault() {
    val meta = modelWithSigns().toEwChartDto(ChartDensity.ALL).meta
    assertNull(meta.risingSign)
    assertNull(meta.sunSign)
    assertNull(meta.moonSign)
  }

  @Test
  fun signs_whenAskedFor_nameOnlyNoDegree() {
    val meta = modelWithSigns().toEwChartDto(ChartDensity.FULL, withSigns = true).meta
    assertEquals(
      listOf(ZodiacSign.CANCER, ZodiacSign.TAURUS, ZodiacSign.LIBRA).map { it.getTitle<ZodiacSign>(Lang.ZH_TW) },
      listOf(meta.risingSign, meta.sunSign, meta.moonSign),
    )
  }

  private fun jd(y: Int, m: Int, d: Int) = TimeTools.getGmtJulDay(LocalDateTime.of(y, m, d, 0, 0))

  /** 1966-07-17 生；第 N 歲止於 (1966+N) 年 2/4（模擬立春） */
  private val liChunAges: Map<Int, Pair<GmtJulDay, GmtJulDay>> = (1..40).associateWith { age ->
    val start = if (age == 1) jd(1966, 7, 17) else jd(1965 + age, 2, 4)
    start to jd(1966 + age, 2, 4)
  }

  private fun modelWithAges(
    ageMap: Map<Int, Pair<GmtJulDay, GmtJulDay>> = liChunAges,
    larges: List<FortuneData> = listOf(
      fortuneOf("丁酉", 10, 19, jd(1975, 7, 1), jd(1985, 7, 1)),
      fortuneOf("戊戌", 20, 29, jd(1985, 7, 1), jd(1995, 7, 1)),
    ),
  ) = fakeModel().let { m ->
    FakeEwModel(m.eightWords, larges, m.risingStemBranch, m.solarTermsTimePos, m.prevSolarSign, m.nextSolarSign, m.score, ageMap = ageMap)
  }

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
  private fun fortuneOf(
    stemBranch: String, from: Int, to: Int,
    start: GmtJulDay = GmtJulDay(2442000.0), end: GmtJulDay = GmtJulDay(2445650.0),
  ) = FortuneData(
    stemBranch = StemBranch[stemBranch],
    startFortuneGmtJulDay = start,
    endFortuneGmtJulDay = end,
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
  override val starPosMap: Map<AstroPoint, PositionWithBranch> = emptyMap(),
  override val rsmiMap: Map<TransPoint, ZodiacDegree> = emptyMap(),
  override val ageMap: Map<Int, Pair<GmtJulDay, GmtJulDay>> = emptyMap(),
) : IPersonContextModel {
  override val gender: Gender = Gender.M
  override val name: String? = "王小明"
  override val place: String? = "台北市"
  override val time: ChronoLocalDateTime<*> = LocalDateTime.of(1966, 7, 17, 1, 3)
  override val location: ILocation = Location.of(25.04, 121.51, "Asia/Taipei")
  override val chineseDate: ChineseDate = ChineseDate(78, StemBranch.丙午, 7, false, 1)
  override val dst: Boolean = false
  override val gmtMinuteOffset: Int = 480
  override val houseMap: Map<Int, ZodiacDegree> = emptyMap()
  override val aspectsDataSet: Set<IPointAspectPattern> = emptySet()
  override val fortuneDataSmalls: List<FortuneData> = emptyList()
  override val childHazards: List<HazardItem> = emptyList()
}
