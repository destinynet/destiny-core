/**
 * 希臘化時主星技法共用的常數：行星的最小年（least / minor years）與 Valens 的年長。
 *
 * Created by smallufo on 2026-11-26.
 */
package destiny.core.astrology.prediction

import destiny.core.astrology.Planet

/**
 * 七星的最小年（least years，又稱 minor years）。
 *
 * 來源是各行星回到同一相位關係的週期（例：金星 8 年、火星 15 年、太陽 19 年的默冬週期、
 * 月亮 25 年的埃及曆週期、土星 30 年）。七者相加 129，正是十年限（Decennials）每段 129 個月的由來；
 * 黃道釋放（Zodiacal Releasing）的星座年數是另一張表（[zodiacalReleasingYears]），以星座為鍵；
 * 兩張表除了摩羯（27，不是土星的 30）都一致，這個關係由測試看守。
 */
val planetLeastYears: Map<Planet, Int> = mapOf(
  Planet.SATURN to 30,
  Planet.JUPITER to 12,
  Planet.MARS to 15,
  Planet.SUN to 19,
  Planet.VENUS to 8,
  Planet.MERCURY to 20,
  Planet.MOON to 25,
)

/** 七星最小年的總和（129） */
val PLANET_LEAST_YEARS_TOTAL: Int = planetLeastYears.values.sum()

/** Egyptian year = 360 days */
const val EGYPTIAN_YEAR_DAYS: Double = 360.0

/** Egyptian month = 30 days */
const val EGYPTIAN_MONTH_DAYS: Double = 30.0

/**
 * 換算成日期時「一年」有幾天。Valens 的技法（黃道釋放、十年限）以 30 天一個月、360 天一年計；
 * 少數作法改用 365.25 天的儒略年。每一層都等比縮放。
 */
enum class ValensYearLength(val days: Double) {
  /** Valens' ideal year of 360 days (a full circle of 360°); the convention most practitioners follow */
  VALENS_360(EGYPTIAN_YEAR_DAYS),
  /** Julian year; a minority convention. A 25-year period ends about 131 days later than with [VALENS_360] */
  JULIAN_365_25(365.25);

  /** 一個月的天數（一年的 1/12） */
  val monthDays: Double get() = days / 12.0

  companion object {
    val DEFAULT = VALENS_360
  }
}

/** 黃道釋放沿用的名稱 */
typealias ZrYearLength = ValensYearLength
