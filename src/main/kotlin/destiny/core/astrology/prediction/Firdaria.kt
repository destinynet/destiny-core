/**
 * Created by smallufo on 2025-08-11.
 */
package destiny.core.astrology.prediction

import destiny.core.astrology.AstroPoint
import destiny.core.astrology.LunarNode
import destiny.core.astrology.Planet
import destiny.core.calendar.GmtJulDay
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class Firdaria(
  val majorRuler: AstroPoint,
  val subRuler: AstroPoint,
  @Contextual
  val fromTime: GmtJulDay,
  @Contextual
  val toTime: GmtJulDay
)

interface IFirdariaPeriod {
  /** 此時期的主宰星 */
  val ruler: AstroPoint

  /** 時期的開始時間 (GMT) */
  val startTime: GmtJulDay

  /** 時期的結束時間 (GMT) */
  val endTime: GmtJulDay
}


/**
 * 法達副運時期 (Sub Period)。
 */
@Serializable
data class FirdariaSubPeriod(
  override val ruler: AstroPoint,
  @Contextual
  override val startTime: GmtJulDay,
  @Contextual
  override val endTime: GmtJulDay
) : IFirdariaPeriod

/**
 * 法達主運時期 (Major Period)。
 * 包含其下的所有副運時期。
 */
@Serializable
data class FirdariaMajorPeriod(
  override val ruler: AstroPoint,
  @Contextual
  override val startTime: GmtJulDay,
  @Contextual
  override val endTime: GmtJulDay,
  /** 此主運下的所有副運列表 */
  val subPeriods: List<FirdariaSubPeriod>
) : IFirdariaPeriod


/**
 * 封裝一個完整的法達時間線模型。
 * 這是 IFirdariaFeature 的最終輸出。
 */
data class FirdariaTimeline(
  /** 是否為日生盤 */
  val diurnal: Boolean,
  /** 完整的主運列表，從出生開始排序 */
  val majorPeriods: List<FirdariaMajorPeriod>
)  {

  /**
   * 方便的查詢功能：根據一個特定時刻，找出當下的主運與副運。
   * @param gmtJulDay 欲查詢的時刻。
   * @return 一個包含當前主運與副運的 Pair，若查詢時間在週期開始前則回傳 null。
   * Pair 的 a 是主運，b 是副運 (如果有的話)。
   */
  fun getRulersAt(gmtJulDay: GmtJulDay): Pair<IFirdariaPeriod, IFirdariaPeriod?>? {
    return majorPeriods.find { gmtJulDay in it.startTime..it.endTime }?.let { majorPeriod ->
      majorPeriod to majorPeriod.subPeriods.find { gmtJulDay in it.startTime..it.endTime }
    }
  }

  fun allPeriods(): List<IFirdariaPeriod> =
    majorPeriods.flatMap { listOf(it) + it.subPeriods }

  fun getPeriodsBetween(from: GmtJulDay, to: GmtJulDay): List<FirdariaMajorPeriod> {
    // 兩個區間 [A, B] 和 [C, D] 重疊的條件是 A <= D 且 C <= B
    return majorPeriods.filter { it.startTime <= to && from <= it.endTime }
  }

  /**
   * 獲取與指定時間範圍 [from, to] 有重疊的所有法達時段。
   */
  fun getPeriods(from: GmtJulDay, to: GmtJulDay): List<Firdaria> {
    return majorPeriods.flatMap { majorPeriod ->
      // 判斷：這是一個有子限期的常規行星主限期，還是無子限期的南北焦點主限期？

      // 案例 A: 常規行星主限期 (subPeriods 列表不為空)
      if (majorPeriod.subPeriods.isNotEmpty()) {
        majorPeriod.subPeriods
          .filter { subPeriod ->
            // 篩選出時間上有重疊的子限期
            subPeriod.startTime < to && from < subPeriod.endTime
          }
          .map { subPeriod ->
            // 將子限期轉換為 Firdaria 物件
            Firdaria(majorPeriod.ruler, subPeriod.ruler, subPeriod.startTime, subPeriod.endTime)
          }
      }
      // 案例 B: 南北焦點主限期 (subPeriods 列表為空)
      else {
        // 直接判斷這個主限期本身是否與查詢範圍重疊
        if (majorPeriod.startTime < to && from < majorPeriod.endTime) {
          // 如果重疊，則創建一個 Firdaria 物件，其 subRuler 就是 majorRuler
          listOf(
            // 對於南北交點，子限主星就是它自己
            Firdaria(majorPeriod.ruler, majorPeriod.ruler, majorPeriod.startTime, majorPeriod.endTime)
          )
        } else {
          emptyList()
        }
      }
    }
  }
}

val majorRulerYearsMap: Map<AstroPoint, Int> = mapOf(
  Planet.SUN to 10,
  Planet.VENUS to 8,
  Planet.MERCURY to 13,
  Planet.MOON to 9,
  Planet.SATURN to 11,
  Planet.JUPITER to 12,
  Planet.MARS to 7,
  LunarNode.NORTH_TRUE to 3,
  LunarNode.SOUTH_TRUE to 2
)

fun getMajorRulers(diurnal: Boolean): List<AstroPoint> = if (diurnal) {
  listOf(Planet.SUN, Planet.VENUS, Planet.MERCURY, Planet.MOON, Planet.SATURN, Planet.JUPITER, Planet.MARS, LunarNode.NORTH_TRUE, LunarNode.SOUTH_TRUE)
} else {
  listOf(Planet.MOON, Planet.SATURN, Planet.JUPITER, Planet.MARS, Planet.SUN, Planet.VENUS, Planet.MERCURY, LunarNode.NORTH_TRUE, LunarNode.SOUTH_TRUE)
}
