/**
 * Created by smallufo on 2026-08-10.
 */
package destiny.core.astrology

import destiny.tools.JSerializable
import kotlinx.serialization.Serializable

/** 符號呈現方式 */
enum class SymbolStyle {
  /** 圖形符號 , 例如 ☉ / ♈ */
  GLYPH,
  /** 文字 , 例如 「太陽」 / 「牡羊」 */
  TEXT
}

/** 圓盤樣式 */
enum class WheelStyle(
  /** 序列化用的字面（`astrolog`／`cusp`）。是持久化的資料契約，不可改名 */
  val code: String
) {
  /** 星座色環（Astrolog 式） */
  ASTROLOG("astrolog"),
  /** 宮頭標示完整度數（Solar Fire 式） */
  CUSP("cusp");

  companion object {
    fun ofCode(code: String?): WheelStyle? = entries.firstOrNull { it.code == code }
  }
}

/** 占星盤的 view 設定，不影響任何計算結果 */
@Serializable
data class HoroscopeViewSettings(

  /** 星體以符號還是文字呈現（尚無 renderer 支援） */
  val pointStyle: SymbolStyle = SymbolStyle.GLYPH,

  /** 星座以符號還是文字呈現（尚無 renderer 支援） */
  val signStyle: SymbolStyle = SymbolStyle.GLYPH,

  /** 是否畫次要相位（相位線與相位表同步） */
  val showMinorAspects: Boolean = false,

  /** 圓盤樣式 */
  val wheelStyle: WheelStyle = WheelStyle.ASTROLOG,

) : JSerializable
