/**
 * Created by smallufo on 2026-08-03.
 */
package destiny.tools

/**
 * 平台中立的語言標示 —— [java.util.Locale] 的替代品，可進入 KMP `commonMain`。
 *
 * ## 為何是「單一 tag」而非 language / country / variant 三欄位
 *
 *  1. **序列化零改動**：`LocaleSerializer` 本來就以 `toLanguageTag()` 進出（`"zh-TW"`），
 *     tag 即現行 wire format。
 *  2. **零配置成本**：本型別會出現在每個 `Descriptive.getTitle()` 上、擴散上百個檔案。
 *     `@JvmInline value class` 包 String 在執行期不配置物件。
 *  3. **不必為 variant / script 做取捨**：tag 照單全收。餵 `"zh-Hant-TW"` 進來，
 *     三個 subtag 就原封不動留在字串裡；三欄位版本則得逐一列舉、將來加 script 要改型別。
 *     （本 repo 的 258 個 .properties 目前只用到 `_zh_CN` / `_en` / `_ja` / `_zh_TW` / `_zh`，
 *     無 variant 亦無 script。）
 *
 * ## 正規化是強制的
 *
 * 本型別的相等性就是 String 相等性 —— 不像 `Locale` 會內部正規化。若容許
 * `Lang("zh_TW")`、`Lang("zh-tw")`、`Lang("zh-TW")` 並存，`Map<Lang, *>` 會直接壞掉。
 * 因此 constructor 為 private，一律走 [of]，正規化為 BCP-47 標準大小寫：
 * 語言小寫、script 首字大寫、地區大寫，以 `-` 相連。
 *
 * @property tag 正規化後的 BCP-47 標籤；[ROOT] 為空字串
 */
@JvmInline
value class Lang private constructor(val tag: String) {

  /** 語言子標籤（小寫）。[ROOT] 為空字串 */
  val language: String
    get() = if (tag.isEmpty()) "" else tag.substringBefore('-')

  /** 文字系統子標籤（4 碼、首字大寫），例如 `Hant` / `Hans`；無則 null */
  val script: String?
    get() = segments().getOrNull(1)?.takeIf { it.isScript() }

  /** 地區子標籤（2 碼字母大寫，或 3 碼數字）；無則 null */
  val region: String?
    get() {
      val segs = segments()
      val idx = if (segs.getOrNull(1)?.isScript() == true) 2 else 1
      return segs.getOrNull(idx)?.takeIf { it.isRegion() }
    }

  /** 其餘的 variant 子標籤（小寫）；無則空 list */
  val variants: List<String>
    get() {
      val segs = segments()
      var i = 1
      if (segs.getOrNull(i)?.isScript() == true) i++
      if (segs.getOrNull(i)?.isRegion() == true) i++
      return if (i >= segs.size) emptyList() else segs.subList(i, segs.size)
    }

  val isRoot: Boolean get() = tag.isEmpty()

  /**
   * resource bundle 的檔名後綴，例如 `zh-TW` → `_zh_TW`、[ROOT] → `""`。
   *
   * 對應 `src/main/resources` 底下 `*_zh_TW.properties` 這類命名。
   */
  fun resourceSuffix(): String = if (tag.isEmpty()) "" else "_" + tag.replace('-', '_')

  /**
   * 由精確到寬鬆的查找順序，逐一截去尾端 subtag，最後補上 [ROOT]。
   *
   * ```
   * zh-TW       → [zh-TW, zh, ROOT]
   * zh-Hant-TW  → [zh-Hant-TW, zh-Hant, zh, ROOT]
   * en          → [en, ROOT]
   * ```
   *
   * 注意這**不完全等同** Java `ResourceBundle` 的候選序 —— 後者會替中文自動補上
   * 隱含的 script（`zh_TW` → `[zh_TW_#Hant, zh__#Hant, zh_TW, zh, ROOT]`）。
   * 但本 repo 沒有任何帶 script 的 .properties，那些額外候選永遠落空，
   * 故實際解析結果一致；`LangTest` 對使用中的語言逐一驗證了這點。
   */
  fun fallbacks(): List<Lang> {
    if (tag.isEmpty()) return listOf(ROOT)
    val segs = segments()
    return buildList {
      for (n in segs.size downTo 1) add(Lang(segs.take(n).joinToString("-")))
      add(ROOT)
    }
  }

  override fun toString(): String = tag

  private fun segments(): List<String> = if (tag.isEmpty()) emptyList() else tag.split('-')

  companion object {

    /** 語言未定（對應 `Locale.ROOT`，其 language tag 為 `"und"`） */
    val ROOT = Lang("")

    val ZH = Lang("zh")
    val ZH_TW = Lang("zh-TW")
    val ZH_CN = Lang("zh-CN")
    val EN = Lang("en")
    val JA = Lang("ja")

    /**
     * 本專案的預設語言。
     *
     * 刻意用常數而非 `expect fun systemLang()` —— 隱式的全域預設在不同平台行為不同，
     * 且此 repo 已被同類問題咬過（`String.format` 未帶 Locale 導致小數點格式隨系統語系跑掉）。
     */
    val DEFAULT = ZH_TW

    /**
     * 解析並正規化。接受底線與連字號兩種分隔、任意大小寫、前後空白，
     * 以及 `Locale.toString()` 的 `#` 擴充標記（`zh_TW_#Hant` 會取 `zh_TW`）。
     *
     * 注意 `Locale.forLanguageTag("zh_TW")` 會回傳空 Locale（Java 只吃連字號），
     * 這正是本方法必須自己處理分隔符號的原因。
     *
     * @return 無法解析（空字串、語言碼不合法）時回傳 null；`"und"` 回傳 [ROOT]
     */
    fun of(raw: String?): Lang? {
      if (raw == null) return null
      val cleaned = raw.trim().substringBefore('#').replace('_', '-')
      val segs = cleaned.split('-').filter { it.isNotEmpty() }
      if (segs.isEmpty()) return null

      val language = segs[0].lowercase()
      if (language == "und") return ROOT
      if (language.length !in 2..8 || !language.all { it in 'a'..'z' }) return null

      val out = mutableListOf(language)
      var i = 1
      segs.getOrNull(i)?.takeIf { it.isScript() }?.let {
        out += it.lowercase().replaceFirstChar { c -> c.titlecaseChar() }
        i++
      }
      segs.getOrNull(i)?.takeIf { it.isRegion() }?.let {
        out += it.uppercase()
        i++
      }
      while (i < segs.size) {
        out += segs[i].lowercase()
        i++
      }
      return Lang(out.joinToString("-"))
    }
  }
}

private fun String.isScript(): Boolean = length == 4 && all { it.isLetter() }

private fun String.isRegion(): Boolean =
  (length == 2 && all { it.isLetter() }) || (length == 3 && all { it.isDigit() })
