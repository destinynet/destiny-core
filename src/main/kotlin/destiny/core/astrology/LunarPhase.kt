/**
 * Created by smallufo on 2022-11-23.
 */
package destiny.core.astrology

/** 月相；[angle] 是月亮領先太陽的黃經度數 */
enum class LunarPhase(val angle: Double) {
  NEW(0.0),
  FIRST_QUARTER(90.0),
  FULL(180.0),
  LAST_QUARTER(270.0)
}
