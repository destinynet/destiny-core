/**
 * Created by smallufo on 2026-11-09.
 */
package destiny.tools

/**
 * 中文的書寫變體：字形（簡／繁）與用語（港澳／台灣）都由它決定。
 * 判斷只有 [of] 這一處 —— 轉簡繁、給模型的書寫指示、挑地區覆蓋檔都由它推，不要各自再比 `country`／`script`
 */
enum class ChineseVariant {
  /** 簡體：明寫 `Hans`，或地區是中國、新加坡 */
  SIMPLIFIED,

  /** 港澳繁體：香港、澳門 */
  HONG_KONG,

  /** 台灣繁體；沒寫地區的中文（`zh`、`zh-Hant`）也算這一種 */
  TAIWAN;

  val isTraditional: Boolean get() = this != SIMPLIFIED

  companion object {
    private val SIMPLIFIED_REGIONS = setOf("CN", "SG")
    private val HONG_KONG_REGIONS = setOf("HK", "MO")

    /** [lang] 的中文變體；不是中文回 null。`Hans` 與簡體地區優先於港澳（`zh-Hans-HK` 是簡體） */
    fun of(lang: Lang): ChineseVariant? = when {
      lang.language != "zh"                                         -> null
      lang.script == "Hans" || lang.region in SIMPLIFIED_REGIONS    -> SIMPLIFIED
      lang.region in HONG_KONG_REGIONS                              -> HONG_KONG
      else                                                          -> TAIWAN
    }
  }
}
