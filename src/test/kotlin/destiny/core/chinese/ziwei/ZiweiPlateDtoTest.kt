/**
 * Created by smallufo on 2026-08-12.
 */
package destiny.core.chinese.ziwei

import destiny.core.DayNight
import destiny.core.Gender
import destiny.core.calendar.chinese.ChineseDate
import destiny.core.chinese.Branch
import destiny.core.chinese.FiveElement
import destiny.core.chinese.StemBranch
import destiny.core.identityFieldsIn
import kotlinx.serialization.Serializable
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

    val dto = plate.toZiweiPlateDto(ZiweiDensity.ALL)

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
    val dto = fourTierPlate().toZiweiPlateDto(ZiweiDensity.COMPACT)

    assertEquals(listOf("紫微"), dto.houses.single().stars.map { it.label })
  }

  @Test
  fun fullDensity_keepsMinorButDropsMiscellaneous() {
    val dto = fourTierPlate().toZiweiPlateDto(ZiweiDensity.FULL)

    assertEquals(setOf("紫微", "文昌", "天刑"), dto.houses.single().stars.map { it.label }.toSet())
  }

  @Test
  fun allDensity_keepsMiscellaneous() {
    val dto = fourTierPlate().toZiweiPlateDto(ZiweiDensity.ALL)

    assertEquals(setOf("紫微", "文昌", "天刑", "博士"), dto.houses.single().stars.map { it.label }.toSet())
  }

  @Test
  fun houses_carryStemBranchAndLocalizedHouseLabel() {
    val dto = twoHousePlate().toZiweiPlateDto(ZiweiDensity.FULL)

    assertEquals(
      mapOf("子" to ("甲子" to "命宮"), "丑" to ("乙丑" to "兄弟")),
      dto.houses.associate { it.branch to (it.stemBranch to it.houseLabel) }
    )
  }

  @Test
  fun mingHouse_flaggedByHouseName() {
    val dto = twoHousePlate().toZiweiPlateDto(ZiweiDensity.FULL)

    assertEquals(listOf("子"), dto.houses.filter { it.isMing }.map { it.branch })
  }

  @Test
  fun bodyHouse_flaggedByStemBranch() {
    val dto = twoHousePlate().toZiweiPlateDto(ZiweiDensity.FULL)

    assertEquals(listOf("丑"), dto.houses.filter { it.isBody }.map { it.branch })
  }

  @Test
  fun houseWithoutMainStar_flaggedEmpty() {
    val dto = twoHousePlate().toZiweiPlateDto(ZiweiDensity.FULL)

    assertEquals(listOf("丑"), dto.houses.filter { it.isEmpty }.map { it.branch })
  }

  /** 空宮判定看的是主星有無，與當前 density 無關 —— compact 之外的星被濾掉不該讓宮位「變空」 */
  @Test
  fun emptyFlag_ignoresDensityFiltering() {
    val plate = plateOf(
      bodyHouse = StemBranch.乙丑,
      houses = arrayOf(houseOf(House.兄弟, StemBranch.乙丑, StarLucky.文昌))
    )

    val dto = plate.toZiweiPlateDto(ZiweiDensity.COMPACT)

    assertEquals(true, dto.houses.single().isEmpty)
  }

  @Test
  fun houses_carryFortuneAgeRange() {
    val dto = twoHousePlate().toZiweiPlateDto(ZiweiDensity.FULL)

    val ming = dto.houses.single { it.branch == "子" }
    assertEquals(4 to 13, ming.fortuneFromAge to ming.fortuneToAge)
  }

  @Test
  fun stars_carryBirthTransFours() {
    val dto = decoratedPlate().toZiweiPlateDto(ZiweiDensity.FULL)

    val ziwei = dto.houses.single().stars.single { it.label == "紫微" }
    assertEquals(listOf("祿"), ziwei.transFours)
  }

  /** 只取本命（[FlowType.MAIN]）四化 —— 大限/流年四化不屬於本命盤呈現 */
  @Test
  fun stars_ignoreNonNatalTransFours() {
    val dto = decoratedPlate().toZiweiPlateDto(ZiweiDensity.FULL)

    val wenchang = dto.houses.single().stars.single { it.label == "文昌" }
    assertEquals(emptyList(), wenchang.transFours)
  }

  @Test
  fun stars_carryStrength() {
    val dto = decoratedPlate().toZiweiPlateDto(ZiweiDensity.FULL)

    val stars = dto.houses.single().stars.associate { it.label to it.strength }
    assertEquals(mapOf("紫微" to 2, "文昌" to null), stars)
  }

  @Test
  fun stars_carryAbbreviation() {
    val dto = decoratedPlate().toZiweiPlateDto(ZiweiDensity.FULL)

    assertEquals("紫", dto.houses.single().stars.single { it.label == "紫微" }.abbr)
  }

  @Test
  fun meta_carriesChartLevelFacts() {
    val dto = twoHousePlate().toZiweiPlateDto(ZiweiDensity.FULL)

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

  private fun houseOf(house: House, stemBranch: StemBranch, vararg stars: ZStar) = HouseData(
    house = house,
    stemBranch = stemBranch,
    stars = stars.toSet(),
    flowHouseMap = emptyMap(),
    transFourFlyMap = emptySet(),
    rangeFromAge = 4,
    rangeToAge = 13,
    smallRanges = emptyList()
  )

  private fun plateOf(
    bodyHouse: StemBranch = StemBranch.甲子,
    houses: Array<HouseData>,
    transFours: Map<ZStar, Map<FlowType, T4Value>> = emptyMap(),
    starStrengthMap: Map<ZStar, Int> = emptyMap()
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
    flowBranchMap = emptyMap(),
    starStrengthMap = starStrengthMap,
    notes = emptyList(),
    vageMap = null,
    rageMap = null,
    summaries = emptyList()
  )
}
