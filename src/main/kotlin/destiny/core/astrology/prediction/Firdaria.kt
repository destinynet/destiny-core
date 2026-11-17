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
  val fromTime: GmtJulDay

  /** 時期的結束時間 (GMT) */
  val toTime: GmtJulDay
}


/**
 * 法達副運時期 (Sub Period)。
 */
@Serializable
data class FirdariaSubPeriod(
  override val ruler: AstroPoint,
  @Contextual
  override val fromTime: GmtJulDay,
  @Contextual
  override val toTime: GmtJulDay
) : IFirdariaPeriod

/**
 * 法達主運時期 (Major Period)。
 * 包含其下的所有副運時期。
 */
@Serializable
data class FirdariaMajorPeriod(
  override val ruler: AstroPoint,
  @Contextual
  override val fromTime: GmtJulDay,
  @Contextual
  override val toTime: GmtJulDay,
  /** 此主運下的所有副運列表 */
  val subPeriods: List<FirdariaSubPeriod>
) : IFirdariaPeriod


internal val firdariaPeriodOverlapping: (FirdariaMajorPeriod, GmtJulDay, GmtJulDay) -> List<Firdaria> = { majorPeriod, from, to ->
  // 處理主運與副運
  if (majorPeriod.subPeriods.isNotEmpty()) {
    // 案例 A: 常規行星主運，有副運
    majorPeriod.subPeriods
      .filter { subPeriod -> subPeriod.fromTime < to && from < subPeriod.toTime } // 區間重疊檢查
      .map { subPeriod -> Firdaria(majorPeriod.ruler, subPeriod.ruler, subPeriod.fromTime, subPeriod.toTime) }
  } else {
    // 案例 B: 南北交點主運，無副運
    if (majorPeriod.fromTime < to && from < majorPeriod.toTime) {
      listOf(Firdaria(majorPeriod.ruler, majorPeriod.ruler, majorPeriod.fromTime, majorPeriod.toTime))
    } else {
      emptyList()
    }
  }
}

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
    return majorPeriods.find { gmtJulDay in it.fromTime..it.toTime }?.let { majorPeriod ->
      majorPeriod to majorPeriod.subPeriods.find { gmtJulDay in it.fromTime..it.toTime }
    }
  }

  fun allPeriods(): List<IFirdariaPeriod> =
    majorPeriods.flatMap { listOf(it) + it.subPeriods }

  fun getPeriodsBetween(from: GmtJulDay, to: GmtJulDay): List<FirdariaMajorPeriod> {
    // 兩個區間 [A, B] 和 [C, D] 重疊的條件是 A <= D 且 C <= B
    return majorPeriods.filter { it.fromTime <= to && from <= it.toTime }
  }

  /** 獲取與指定時間範圍 [from, to] 有重疊的所有法達時段。 */
  fun getPeriods(from: GmtJulDay, to: GmtJulDay): List<Firdaria> {
    return majorPeriods.flatMap { firdariaPeriodOverlapping.invoke(it, from, to) }
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
  LunarNode.NORTH to 3,
  LunarNode.SOUTH to 2
)

/**
 * 法達主運的排列（Firdaria sequence）。
 *
 * 日生盤各派相同：日→金→水→月→土→木→火→北交→南交。
 * 夜生盤的七星順序也相同（月→土→木→火→日→金→水），**分歧只在南北交點插在哪裡**，見 [FirdariaAbuMashar]、[FirdariaBonatti]。
 * 副運只取七星、順序與主運相同，從該主運的主星起算；兩派一致。
 */
interface IFirdariaSequence {
  val variant: FirdariaVariant

  /** 一輪（75 年）的主運主星，含南北交點 */
  fun majorRulers(diurnal: Boolean): List<AstroPoint>
}

/** 日生盤的主運順序：各派相同 */
private val DIURNAL_RULERS: List<AstroPoint> =
  listOf(Planet.SUN, Planet.VENUS, Planet.MERCURY, Planet.MOON, Planet.SATURN, Planet.JUPITER, Planet.MARS, LunarNode.NORTH, LunarNode.SOUTH)

/**
 * Abu Ma'shar：夜生盤的南北交點也排在最後（水星之後，70–75 歲）。
 * al-Qabisi、al-Biruni 與多數現代譯介（Robert Hand）採此排法，一般視為最早、也最通行的版本 —— 預設值。
 */
data object FirdariaAbuMashar : IFirdariaSequence {
  override val variant = FirdariaVariant.ABU_MASHAR

  override fun majorRulers(diurnal: Boolean): List<AstroPoint> = if (diurnal) DIURNAL_RULERS
  else listOf(Planet.MOON, Planet.SATURN, Planet.JUPITER, Planet.MARS, Planet.SUN, Planet.VENUS, Planet.MERCURY, LunarNode.NORTH, LunarNode.SOUTH)
}

/**
 * Bonatti：夜生盤的南北交點緊接在火星之後（與日生盤一樣「跟在火星後面」），之後才是日、金、水。
 * Robert Zoller 一派與部分軟體採此排法。
 */
data object FirdariaBonatti : IFirdariaSequence {
  override val variant = FirdariaVariant.BONATTI

  override fun majorRulers(diurnal: Boolean): List<AstroPoint> = if (diurnal) DIURNAL_RULERS
  else listOf(Planet.MOON, Planet.SATURN, Planet.JUPITER, Planet.MARS, LunarNode.NORTH, LunarNode.SOUTH, Planet.SUN, Planet.VENUS, Planet.MERCURY)
}

/** 法達排法的識別（序列化、設定值用）；實作見 [sequence] */
enum class FirdariaVariant {
  ABU_MASHAR,
  BONATTI;

  val sequence: IFirdariaSequence
    get() = when (this) {
      ABU_MASHAR -> FirdariaAbuMashar
      BONATTI    -> FirdariaBonatti
    }

  companion object {
    val DEFAULT = ABU_MASHAR
  }
}
