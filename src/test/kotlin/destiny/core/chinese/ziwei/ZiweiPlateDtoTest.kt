/**
 * Created by smallufo on 2026-08-12.
 */
package destiny.core.chinese.ziwei

import destiny.core.ChartDensity
import destiny.core.IntAgeNote
import destiny.core.IntAgeNoteMinguoYearImpl
import destiny.core.chinese.AgeType
import destiny.core.DayNight
import destiny.core.Gender
import destiny.core.calendar.chinese.ChineseDate
import destiny.core.chinese.Branch
import destiny.core.chinese.FiveElement
import destiny.core.chinese.StemBranch
import destiny.core.identityFieldsIn
import com.jayway.jsonpath.JsonPath
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

/** 模擬「有人不小心把身分欄位加進盤面 DTO」，供 [ZiweiPlateDtoTest.identityGuard_catchesLeakedField] 使用 */
@Serializable
private data class LeakyPlateDto(val label: String, val name: String)

class ZiweiPlateDtoTest {

  @Test
  fun stars_carryTierFromSealedType() {
    val plate = plateOf(
      houses = arrayOf(
        houseOf(
          House.命宮, StemBranch.甲子,
          StarMain.紫微, StarLucky.文昌, StarUnlucky.擎羊, StarMinor.天刑, StarDoctor.博士
        )
      )
    )

    val dto = plate.toZiweiPlateDto(ChartDensity.ALL)

    val tiers = dto.houses.single { it.branch == "子" }.stars.associate { it.label to it.tier }
    assertEquals(
      mapOf(
        "紫微" to "MAIN",
        "文昌" to "LUCKY",
        "擎羊" to "UNLUCKY",
        "天刑" to "MINOR",
        "博士" to "DOCTOR"
      ), tiers
    )
  }

  /**
   * 先證明 guard 真的抓得到 —— 否則「[ZiweiPlateDto] 乾淨」那條就只是空轉。
   */
  @Test
  fun identityGuard_catchesLeakedField() {
    assertEquals(
      listOf("${LeakyPlateDto::class.qualifiedName}.name"),
      identityFieldsIn(LeakyPlateDto.serializer().descriptor)
    )
  }

  @Test
  fun ziweiPlateDto_declaresNoIdentityField() {
    assertEquals(emptyList(), identityFieldsIn(ZiweiPlateDto.serializer().descriptor))
  }

  @Test
  fun compactDensity_keepsOnlyMainStars() {
    val dto = fourTierPlate().toZiweiPlateDto(ChartDensity.COMPACT)

    assertEquals(listOf("紫微"), dto.houses.single().stars.map { it.label })
  }

  @Test
  fun fullDensity_keepsMinorButDropsMiscellaneous() {
    val dto = fourTierPlate().toZiweiPlateDto(ChartDensity.FULL)

    assertEquals(setOf("紫微", "文昌", "天刑"), dto.houses.single().stars.map { it.label }.toSet())
  }

  @Test
  fun allDensity_keepsMiscellaneous() {
    val dto = fourTierPlate().toZiweiPlateDto(ChartDensity.ALL)

    assertEquals(setOf("紫微", "文昌", "天刑", "博士"), dto.houses.single().stars.map { it.label }.toSet())
  }

  @Test
  fun houses_carryStemBranchAndLocalizedHouseLabel() {
    val dto = twoHousePlate().toZiweiPlateDto(ChartDensity.FULL)

    assertEquals(
      mapOf("子" to ("甲子" to "命宮"), "丑" to ("乙丑" to "兄弟")),
      dto.houses.associate { it.branch to (it.stemBranch to it.houseLabel) }
    )
  }

  @Test
  fun mingHouse_flaggedByHouseName() {
    val dto = twoHousePlate().toZiweiPlateDto(ChartDensity.FULL)

    assertEquals(listOf("子"), dto.houses.filter { it.isMing }.map { it.branch })
  }

  @Test
  fun bodyHouse_flaggedByStemBranch() {
    val dto = twoHousePlate().toZiweiPlateDto(ChartDensity.FULL)

    assertEquals(listOf("丑"), dto.houses.filter { it.isBody }.map { it.branch })
  }

  @Test
  fun houseWithoutMainStar_flaggedEmpty() {
    val dto = twoHousePlate().toZiweiPlateDto(ChartDensity.FULL)

    assertEquals(listOf("丑"), dto.houses.filter { it.isEmpty }.map { it.branch })
  }

  /** 空宮判定看的是主星有無，與當前 density 無關 —— compact 之外的星被濾掉不該讓宮位「變空」 */
  @Test
  fun emptyFlag_ignoresDensityFiltering() {
    val plate = plateOf(
      bodyHouse = StemBranch.乙丑,
      houses = arrayOf(houseOf(House.兄弟, StemBranch.乙丑, StarLucky.文昌))
    )

    val dto = plate.toZiweiPlateDto(ChartDensity.COMPACT)

    assertEquals(true, dto.houses.single().isEmpty)
  }

  @Test
  fun houses_carryFortuneAgeRange() {
    val dto = twoHousePlate().toZiweiPlateDto(ChartDensity.FULL)

    val ming = dto.houses.single { it.branch == "子" }
    assertEquals(4 to 13, ming.fortuneFromAge to ming.fortuneToAge)
  }

  @Test
  fun stars_carryBirthTransFours() {
    val dto = decoratedPlate().toZiweiPlateDto(ChartDensity.FULL)

    val ziwei = dto.houses.single().stars.single { it.label == "紫微" }
    assertEquals(listOf(T4Value.祿), ziwei.transFours)
  }

  /** 只取本命（[FlowType.MAIN]）四化 —— 大限/流年四化不屬於本命盤呈現 */
  @Test
  fun stars_ignoreNonNatalTransFours() {
    val dto = decoratedPlate().toZiweiPlateDto(ChartDensity.FULL)

    val wenchang = dto.houses.single().stars.single { it.label == "文昌" }
    assertEquals(emptyList(), wenchang.transFours)
  }

  @Test
  fun stars_carryStrength() {
    val dto = decoratedPlate().toZiweiPlateDto(ChartDensity.FULL)

    val stars = dto.houses.single().stars.associate { it.label to it.strength }
    assertEquals(mapOf("紫微" to 2, "文昌" to null), stars)
  }

  /**
   * 廟旺弱陷給的是**文字**，不是不透明的整數 —— 1→廟、2→旺 的對應屬領域知識（[IStrength]），
   * 不該外流到 renderer 各自實作一份。原始整數仍保留供樣式分級使用。
   */
  @Test
  fun stars_carryStrengthLabel() {
    val dto = decoratedPlate().toZiweiPlateDto(ChartDensity.FULL)

    val stars = dto.houses.single().stars.associate { it.label to it.strengthLabel }
    assertEquals(mapOf("紫微" to "旺", "文昌" to null), stars)
  }

  @Test
  fun stars_carryAbbreviation() {
    val dto = decoratedPlate().toZiweiPlateDto(ChartDensity.FULL)

    assertEquals("紫", dto.houses.single().stars.single { it.label == "紫微" }.abbr)
  }

  @Test
  fun meta_carriesChartLevelFacts() {
    val dto = twoHousePlate().toZiweiPlateDto(ChartDensity.FULL)

    assertEquals(
      ZiweiMetaDto(
        gender = "M",
        yearStemBranch = "甲子",
        hourBranch = "子",
        dayNight = "DAY",
        fiveElement = "木",
        state = 3,
        mainStarLabel = "紫微",
        bodyStarLabel = "天府",
        mingBranch = "子",
        bodyBranch = "丑"
      ), dto.meta
    )
  }

  /** 疊加盤的 `house` 會被最後一層流運覆寫 —— 宮名與命宮判斷一律看本命（flowHouseMap[MAIN]） */
  @Test
  fun houseLabel_followsNatalHouseEvenWhenOverwritten() {
    val plate = plateOf(houses = arrayOf(
      houseOf(House.兄弟, StemBranch.甲子, StarMain.紫微, flowHouseMap = mapOf(FlowType.MAIN to House.命宮, FlowType.YEAR to House.兄弟))
    ))
    val h = plate.toZiweiPlateDto(ChartDensity.FULL).houses.single()
    assertEquals("命宮", h.houseLabel)
    assertEquals(true, h.isMing)
    assertEquals("子", plate.toZiweiPlateDto(ChartDensity.FULL).meta.mingBranch)
  }

  @Test
  fun houses_carryFlies() {
    val plate = plateOf(houses = arrayOf(
      houseOf(House.命宮, StemBranch.甲子, StarMain.紫微,
              flies = setOf(Triple(T4Value.祿, StarMain.廉貞, Branch.寅), Triple(T4Value.忌, StarMain.太陽, Branch.子)))
    ))
    val flies = plate.toZiweiPlateDto(ChartDensity.FULL).houses.single().flies
    assertEquals(
      setOf(ZiweiFlyDto(T4Value.祿, "廉貞", "寅"), ZiweiFlyDto(T4Value.忌, "太陽", "子")),
      flies.toSet()
    )
  }

  @Test
  fun houses_carrySmallRanges() {
    val plate = plateOf(houses = arrayOf(houseOf(House.命宮, StemBranch.甲子, StarMain.紫微, smallRanges = listOf(1, 13, 25))))
    assertEquals(listOf(1, 13, 25), plate.toZiweiPlateDto(ChartDensity.FULL).houses.single().smallRanges)
  }

  /** 不帶 yearsBy ＝不輸出任何年份（年份配虛歲可推出確切出生年） */
  @Test
  fun years_absentByDefault() {
    val h = twoHousePlate().toZiweiPlateDto(ChartDensity.FULL).houses.first()
    assertEquals(null to null, h.fortuneFromYear to h.fortuneToYear)
  }

  /** fixture 生年 = cycle 78 甲子 = 1984；大限 4–13 */
  @Test
  fun years_virtualAge() {
    val h = twoHousePlate().toZiweiPlateDto(ChartDensity.FULL, yearsBy = AgeType.VIRTUAL).houses.first()
    assertEquals(1987 to 1996, h.fortuneFromYear to h.fortuneToYear)
  }

  @Test
  fun years_realAge() {
    val h = twoHousePlate().toZiweiPlateDto(ChartDensity.FULL, yearsBy = AgeType.REAL).houses.first()
    assertEquals(1988 to 1997, h.fortuneFromYear to h.fortuneToYear)
  }

  /** 年份註記由 core 給（[IntAgeNote.noteOf]），renderer 不自己算民國年 */
  @Test
  fun years_carryNotes() {
    val h = twoHousePlate().toZiweiPlateDto(ChartDensity.FULL, yearsBy = AgeType.VIRTUAL).houses.first()
    assertEquals(mapOf(IntAgeNote.WestYear to "1987", IntAgeNote.Minguo to "民76"), h.fortuneFromNotes)
    assertEquals(mapOf(IntAgeNote.WestYear to "1996", IntAgeNote.Minguo to "民85"), h.fortuneToNotes)
  }

  @Test
  fun years_noNotesByDefault() {
    val h = twoHousePlate().toZiweiPlateDto(ChartDensity.FULL).houses.first()
    assertEquals(emptyMap(), h.fortuneFromNotes)
  }

  /** 民國元年前不註記民國年（與 [IntAgeNoteMinguoYearImpl] 同一份規則） */
  @Test
  fun minguoNote_absentBefore1912() {
    assertEquals(null, IntAgeNote.Minguo.noteOf(1911))
    assertEquals("民1", IntAgeNote.Minguo.noteOf(1912))
    assertEquals("1911", IntAgeNote.WestYear.noteOf(1911))
  }

  /** 流運宮名的簡稱取 [House.abbr]：夫妻＝偶、交友＝奴，不是取首字 */
  @Test
  fun flowHouseAbbrs_useTraditionalAbbr() {
    val plate = plateOf(
      houses = arrayOf(houseOf(House.命宮, StemBranch.甲子, StarMain.紫微,
        flowHouseMap = mapOf(FlowType.MAIN to House.命宮, FlowType.SECTION to House.夫妻, FlowType.YEAR to House.交友))),
      flowBranchMap = mapOf(FlowType.SECTION to StemBranch.乙丑, FlowType.YEAR to StemBranch.丙寅),
    )
    val h = plate.toZiweiPlateDto(ChartDensity.FULL).houses.single()
    assertEquals(mapOf(FlowType.SECTION to "夫妻", FlowType.YEAR to "交友"), h.flowHouseLabels)
    assertEquals(mapOf(FlowType.SECTION to "偶", FlowType.YEAR to "奴"), h.flowHouseAbbrs)
  }

  /** 本命盤沒有流運層 */
  @Test
  fun natalPlate_hasNoFlows() {
    val dto = decoratedPlate().toZiweiPlateDto(ChartDensity.FULL)
    assertEquals(emptyList(), dto.flows)
    assertEquals(emptyMap(), dto.houses.single().stars.single { it.label == "文昌" }.flowTransFours)
  }

  /** enum 改型別後線上格式不變：四化仍是 "祿" 字串 */
  @Test
  fun transFours_wireFormatUnchanged() {
    val text = Json.encodeToString(ZiweiPlateDto.serializer(), decoratedPlate().toZiweiPlateDto(ChartDensity.FULL))
    assertEquals(listOf("祿"), JsonPath.read<List<String>>(text, "$.houses[0].stars[?(@.label=='紫微')].transFours[0]"))
  }

  // ---- fixtures ----

  /** 一宮兩星，紫微帶本命化祿與廟旺值、文昌只帶大限四化（不該出現於本命） */
  private fun decoratedPlate() = plateOf(
    houses = arrayOf(houseOf(House.命宮, StemBranch.甲子, StarMain.紫微, StarLucky.文昌)),
    transFours = mapOf(
      StarMain.紫微 to mapOf(FlowType.MAIN to T4Value.祿),
      StarLucky.文昌 to mapOf(FlowType.SECTION to T4Value.忌)
    ),
    starStrengthMap = mapOf(StarMain.紫微 to 2)
  )

  /** 一宮四級：主星 / 六吉 / 乙級雜曜 / 博士十二神 */
  private fun fourTierPlate() = plateOf(
    houses = arrayOf(houseOf(House.命宮, StemBranch.甲子, StarMain.紫微, StarLucky.文昌, StarMinor.天刑, StarDoctor.博士))
  )

  /** 命宮（甲子，有主星）＋兄弟宮（乙丑，空宮且為身宮）—— 三個旗標互不重疊，可分別驗證 */
  private fun twoHousePlate() = plateOf(
    bodyHouse = StemBranch.乙丑,
    houses = arrayOf(
      houseOf(House.命宮, StemBranch.甲子, StarMain.紫微),
      houseOf(House.兄弟, StemBranch.乙丑, StarLucky.文昌)
    )
  )

  private fun houseOf(
    house: House, stemBranch: StemBranch, vararg stars: ZStar,
    flowHouseMap: Map<FlowType, House> = emptyMap(),
    flies: Set<Triple<T4Value, ZStar, Branch>> = emptySet(),
    smallRanges: List<Int> = emptyList(),
  ) = HouseData(
    house = house,
    stemBranch = stemBranch,
    stars = stars.toSet(),
    flowHouseMap = flowHouseMap,
    transFourFlyMap = flies,
    rangeFromAge = 4,
    rangeToAge = 13,
    smallRanges = smallRanges
  )

  private fun plateOf(
    bodyHouse: StemBranch = StemBranch.甲子,
    houses: Array<HouseData>,
    transFours: Map<ZStar, Map<FlowType, T4Value>> = emptyMap(),
    starStrengthMap: Map<ZStar, Int> = emptyMap(),
    flowBranchMap: Map<FlowType, StemBranch> = emptyMap(),
  ): IPlate = Plate(
    name = null,
    chineseDate = ChineseDate(78, StemBranch.甲子, 1, false, 1),
    localDateTime = null,
    year = StemBranch.甲子,
    finalMonthNumForMonthStars = 1,
    hour = Branch.子,
    location = null,
    place = null,
    dayNight = DayNight.DAY,
    gender = Gender.M,
    bodyHouse = bodyHouse,
    mainStar = StarMain.紫微,
    bodyStar = StarMain.天府,
    fiveElement = FiveElement.木,
    state = 3,
    houseDataSet = houses.toSet(),
    transFours = transFours,
    flowBranchMap = flowBranchMap,
    starStrengthMap = starStrengthMap,
    notes = emptyList(),
    vageMap = null,
    rageMap = null,
    summaries = emptyList()
  )
}
