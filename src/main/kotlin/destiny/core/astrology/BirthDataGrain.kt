package destiny.core.astrology

enum class BirthDataGrain {
  DAY , MINUTE
}

/**
 * 是否包含 Axis 點 (ASC/MC) 的計算
 * 只有在有精確出生時間 (MINUTE) 時，Axis 點才有意義
 */
val BirthDataGrain.includeAxis: Boolean
  get() = this == BirthDataGrain.MINUTE

/**
 * 是否計算 Firdaria (法達星限)
 * Firdaria 需要判斷日生/夜生，必須知道太陽在地平線上或下，因此需要精確時間
 */
val BirthDataGrain.includeFirdaria: Boolean
  get() = this == BirthDataGrain.MINUTE

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
