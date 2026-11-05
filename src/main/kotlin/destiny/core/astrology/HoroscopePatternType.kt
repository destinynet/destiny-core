/**
 * Created by smallufo on 2026-11-05.
 */
package destiny.core.astrology

import kotlinx.serialization.Serializable

/**
 * 格局的種類：[AstroPattern.type]，也是 [HoroscopePatternDto.type]（JSON 就是名稱，TS 鏡像照抄）。
 *
 * 顯示名在 `HoroscopePatternType*.properties`（`getTitle(lang)`）—— 格局名稱只有這一份；
 * `AstroPattern*.properties` 只放說明的模板（[AstroPatternTranslator]）
 */
@Serializable
enum class HoroscopePatternType {
  GRAND_TRINE, KITE, T_SQUARE, YOD, BOOMERANG, GOLDEN_YOD, GRAND_CROSS, DOUBLE_T, HEXAGON,
  WEDGE, MYSTIC_RECTANGLE, PENTAGRAM, STELLIUM_SIGN, STELLIUM_HOUSE, CONFRONTATION,
}
