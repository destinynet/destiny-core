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

/** 占星盤的 view 設定，不影響任何計算結果 */
@Serializable
data class HoroscopeViewSettings(

  /** 星體以符號還是文字呈現 */
  val pointStyle: SymbolStyle = SymbolStyle.GLYPH,

  /** 星座以符號還是文字呈現 */
  val signStyle: SymbolStyle = SymbolStyle.GLYPH

) : JSerializable
