/**
 * Created by smallufo on 2025-08-17.
 */
package destiny.core.astrology.prediction

import destiny.core.astrology.Planet
import destiny.core.astrology.ZodiacSign
import destiny.core.calendar.GmtJulDay
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class Profection(
  val annualLord : Planet,
  val annualAscSign : ZodiacSign,
  val annualHouse : Int,
  val monthLord : Planet,
  val monthAscSign : ZodiacSign,
  val monthHouse : Int,
  @Contextual
  val fromTime: GmtJulDay,
  @Contextual
  val toTime: GmtJulDay
)

/**
 * 代表一個單一的月度小限時期。
 * 這是從年度小限中細分出來的月度主題。
 */
@Serializable
data class MonthlyProfectionPeriod(
  /** 該月份的索引 (0-11, 0 代表年度開始的第一個月) */
  val monthIndex: Int,

  /** 該月份輪到的小限宮位 (1 到 12) */
  val profectedHouse: Int,

  /** 該月份小限命宮的星座 */
  val profectedAscendantSign: ZodiacSign,

  /**
   * 月度主星 (Lord of the Month)
   * 也就是該月份小限命宮星座的傳統守護星。
   */
  val lordOfMonth: Planet,

  /** 此月度小限週期的開始時間 */
  @Contextual
  val fromTime: GmtJulDay,

  /** 此月度小限週期的結束時間 */
  @Contextual
  val toTime: GmtJulDay
)

/**
 * 代表一個單一的年度小限時期。
 * 這是構成完整小限法時間線的基本單位。
 */
@Serializable
data class AnnualProfectionPeriod(
  /** 該年度開始時的實足年齡 (例如，出生第一年為 0 歲) */
  val age: Int,

  /** 該年度輪到的小限宮位 (1 到 12) */
  val profectedHouse: Int,

  /** 小限命宮的星座 (Profected Ascendant Sign) */
  val profectedAscendantSign: ZodiacSign,

  /**
   * 年度主星 (Lord of the Year)
   * 也就是小限命宮星座的傳統守護星。
   * 這是該年度最重要的行星。
   */
  val lordOfYear: Planet,

  /** 此年度小限週期的開始時間 (通常是生日當天) */
  @Contextual
  val fromTime: GmtJulDay,

  /** 此年度小限週期的結束時間 (下一個生日前夕) */
  @Contextual
  val toTime: GmtJulDay,

  /** 此年度下的所有月度小限時期列表 */
  val monthlyPeriods: List<MonthlyProfectionPeriod>
)

/**
 * 封裝一個完整的年度小限法時間線模型。
 * 這是計算小限法功能的最終輸出。
 */
@Serializable
data class AnnualProfectionTimeline(
  /** 依年齡排序的、完整的小限時期列表 */
  val periods: List<AnnualProfectionPeriod>
) {

  /**
   * 方便的查詢功能：根據一個特定時刻，找出當下有效的小限時期。
   * @param gmtJulDay 欲查詢的時刻。
   * @return 作用中的 AnnualProfectionPeriod，若查詢時間超出範圍則回傳 null。
   */
  fun getPeriodAt(gmtJulDay: GmtJulDay): AnnualProfectionPeriod? {
    // 找出 gmtJulDay 所在的 fromTime 與 toTime 區間
    return periods.find { gmtJulDay >= it.fromTime && gmtJulDay < it.toTime }
  }

  /**
   * 方便的查詢功能：根據一個特定時刻，找出當下有效的月度小限時期。
   * @param gmtJulDay 欲查詢的時刻。
   * @return 作用中的 MonthlyProfectionPeriod，若查詢時間超出範圍則回傳 null。
   */
  fun getMonthlyPeriodAt(gmtJulDay: GmtJulDay): MonthlyProfectionPeriod? {
    return getPeriodAt(gmtJulDay)?.monthlyPeriods?.find { gmtJulDay >= it.fromTime && gmtJulDay < it.toTime }
  }

  /**
   * 方便的查詢功能：根據一個時間範圍，找出所有與之重疊的小限時期。
   * @param from 查詢範圍的開始時間。
   * @param to 查詢範圍的結束時間。
   * @return 一個包含所有重疊的 AnnualProfectionPeriod 的列表。
   */
  fun getPeriodsBetween(from: GmtJulDay, to: GmtJulDay): List<AnnualProfectionPeriod> {
    // 篩選出時間上有重疊的時期 (區間重疊條件: A.start < B.end AND B.start < A.end)
    return periods.filter { it.fromTime < to && from < it.toTime }
  }

  /**
   * 獲取與指定時間範圍 [from, to] 有重疊的所有小限時段（精確到月）。
   * 這個函式會回傳一個扁平化的列表，其中每個元素都包含了該時段的年度和月度主星資訊。
   * @param from 查詢範圍的開始時間。
   * @param to 查詢範圍的結束時間。
   * @return 一個包含所有重疊的 Profection 物件的列表。
   */
  fun getPeriods(from: GmtJulDay, to: GmtJulDay): List<Profection> {
    return periods.flatMap { annualPeriod ->
      // 篩選出與查詢範圍有重疊的「月度」小限
      annualPeriod.monthlyPeriods
        .filter { monthlyPeriod ->
          // 經典的區間重疊條件: A.start < B.end AND B.start < A.end
          monthlyPeriod.fromTime < to && from < monthlyPeriod.toTime
        }
        .map { monthlyPeriod ->
          // 將年度和月度資訊合併為一個扁平化的 Profection 物件
          Profection(
            annualLord = annualPeriod.lordOfYear,
            annualAscSign = annualPeriod.profectedAscendantSign,
            annualHouse = annualPeriod.profectedHouse,
            monthLord = monthlyPeriod.lordOfMonth,
            monthAscSign = monthlyPeriod.profectedAscendantSign,
            monthHouse = monthlyPeriod.profectedHouse,
            fromTime = monthlyPeriod.fromTime,
            toTime = monthlyPeriod.toTime
          )
        }
    }
  }
}
