/**
 * Created by smallufo on 2025-08-11.
 */
package destiny.core.astrology.prediction

import destiny.core.astrology.AstroPoint
import destiny.core.astrology.LunarNode
import destiny.core.astrology.Planet
import destiny.core.calendar.GmtJulDay
import java.io.Serializable


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
data class FirdariaSubPeriod(
  override val ruler: AstroPoint,
  override val startTime: GmtJulDay,
  override val endTime: GmtJulDay
) : IFirdariaPeriod

/**
 * 法達主運時期 (Major Period)。
 * 包含其下的所有副運時期。
 */
data class FirdariaMajorPeriod(
  override val ruler: AstroPoint,
  override val startTime: GmtJulDay,
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
  val isDiurnal: Boolean,
  /** 完整的主運列表，從出生開始排序 */
  val majorPeriods: List<FirdariaMajorPeriod>
) : Serializable {

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
