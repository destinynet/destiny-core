package destiny.core.astrology

import destiny.core.DayNight

/**
 * 出生資料的時刻精度。總序：[DAY] < [DAY_NIGHT_DIURNAL] / [DAY_NIGHT_NOCTURNAL] < [MINUTE]。
 *
 * DAY_NIGHT 兩個 entry 表達「不知幾點幾分、但至少知道晝生或夜生」，[dayNight] 為各 entry 固定的
 * payload（enum entry 是 singleton，故以兩個 entry 枚舉展開，而非讓單一 entry 攜帶可變值）——
 * 足以解鎖只依晝夜生的技術（如 Firdaria），但 ASC／宮位仍不可得。
 */
enum class BirthDataGrain(val dayNight: DayNight? = null) {
  DAY,

  /** 知晝夜：晝生 */
  DAY_NIGHT_DIURNAL(DayNight.DAY),

  /** 知晝夜：夜生 */
  DAY_NIGHT_NOCTURNAL(DayNight.NIGHT),

  MINUTE
}

/**
 * 是否包含 Axis 點 (ASC/MC) 的計算
 * 只有在有精確出生時間 (MINUTE) 時，Axis 點才有意義
 */
val BirthDataGrain.includeAxis: Boolean
  get() = this == BirthDataGrain.MINUTE

/**
 * 是否計算 Firdaria (法達星限)
 * Firdaria 只需判斷日生/夜生：[BirthDataGrain.MINUTE] 由盤面太陽位置推導；
 * DAY_NIGHT 兩級由 [BirthDataGrain.dayNight] 直接注入（序列以年計，正午錨定的 ±12h 誤差可忽略）
 */
val BirthDataGrain.includeFirdaria: Boolean
  get() = this != BirthDataGrain.DAY

/**
 * 是否計算 Profection (小限)
 * Profection 需要宮位資訊，因此需要精確時間
 */
val BirthDataGrain.includeProfection: Boolean
  get() = this == BirthDataGrain.MINUTE

/**
 * 是否計算 Lunar Returns (月返照)
 * 月返照盤需要精確的宮位，因此需要精確時間
 */
val BirthDataGrain.includeLunarReturns: Boolean
  get() = this == BirthDataGrain.MINUTE
