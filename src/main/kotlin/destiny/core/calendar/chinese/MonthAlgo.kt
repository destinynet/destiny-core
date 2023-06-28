package destiny.core.calendar.chinese

/** 月份演算法  */
enum class MonthAlgo {
  MONTH_FIXED_THIS,   // 不論有無閏月，一律固定當作本月
  MONTH_LEAP_NEXT,    // 若閏月，一律當作下月 (全書)
  MONTH_LEAP_SPLIT15, // 若閏月，15日(含)之前當本月，之後當下月
  MONTH_SOLAR_TERMS;  // 節氣盤
}
