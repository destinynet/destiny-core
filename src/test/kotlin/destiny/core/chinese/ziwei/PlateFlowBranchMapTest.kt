/**
 * Created by smallufo on 2026-07-07.
 */
package destiny.core.chinese.ziwei

import destiny.core.DayNight
import destiny.core.Gender
import destiny.core.calendar.chinese.ChineseDate
import destiny.core.chinese.Branch
import destiny.core.chinese.FiveElement
import destiny.core.chinese.StemBranch
import kotlin.test.Test
import kotlin.test.assertEquals

class PlateFlowBranchMapTest {

  private val natal: IPlate = Plate(
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
    bodyHouse = StemBranch.甲子,
    mainStar = StarMain.紫微,
    bodyStar = StarMain.天府,
    fiveElement = FiveElement.木,
    state = 3,
    houseDataSet = emptySet(),
    transFours = emptyMap(),
    flowBranchMap = emptyMap(),
    starStrengthMap = emptyMap(),
    notes = emptyList(),
    vageMap = null,
    rageMap = null,
    summaries = emptyList()
  )

  private val sectionPlate = PlateWithSection(natal, StemBranch.甲辰, emptyMap(), emptySet(), emptyMap())
  private val yearPlate = PlateWithYear(sectionPlate, StemBranch.丙辰, emptyMap(), emptySet(), emptyMap())
  private val monthPlate = PlateWithMonth(yearPlate, StemBranch.丁巳, emptyMap(), emptySet(), emptyMap())
  private val dayPlate = PlateWithDay(monthPlate, StemBranch.戊午, emptyMap(), emptySet(), emptyMap())
  private val hourPlate = PlateWithHour(dayPlate, StemBranch.己未, emptyMap(), emptySet(), emptyMap())

  @Test
  fun sectionPlate_containsSection() {
    assertEquals(mapOf(FlowType.SECTION to StemBranch.甲辰), sectionPlate.flowBranchMap)
  }

  @Test
  fun yearPlate_containsSectionAndYear() {
    assertEquals(
      mapOf(
        FlowType.SECTION to StemBranch.甲辰,
        FlowType.YEAR to StemBranch.丙辰
      ), yearPlate.flowBranchMap
    )
  }

  @Test
  fun monthPlate_containsSectionYearMonth() {
    assertEquals(
      mapOf(
        FlowType.SECTION to StemBranch.甲辰,
        FlowType.YEAR to StemBranch.丙辰,
        FlowType.MONTH to StemBranch.丁巳
      ), monthPlate.flowBranchMap
    )
  }

  @Test
  fun dayPlate_containsSectionYearMonthDay() {
    assertEquals(
      mapOf(
        FlowType.SECTION to StemBranch.甲辰,
        FlowType.YEAR to StemBranch.丙辰,
        FlowType.MONTH to StemBranch.丁巳,
        FlowType.DAY to StemBranch.戊午
      ), dayPlate.flowBranchMap
    )
  }

  @Test
  fun hourPlate_containsAllFiveFlows() {
    assertEquals(
      mapOf(
        FlowType.SECTION to StemBranch.甲辰,
        FlowType.YEAR to StemBranch.丙辰,
        FlowType.MONTH to StemBranch.丁巳,
        FlowType.DAY to StemBranch.戊午,
        FlowType.HOUR to StemBranch.己未
      ), hourPlate.flowBranchMap
    )
  }
}
