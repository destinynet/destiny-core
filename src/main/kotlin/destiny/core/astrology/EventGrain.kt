package destiny.core.astrology

/**
 * **事件本身**的時刻精度，與本命的 [BirthDataGrain] 正交 ——
 * 本命只知日期、事件知道幾點幾分（或反之）都是常態，兩者不該共用同一個型別。
 *
 * 總序：[MONTH] < [DAY] < [MINUTE]。
 *
 * 這個概念在被命名之前，已經以兩種替代形式各自存在過：去識別化的訊號比對裡拼成
 * 字串前綴（`"DAY:"` / `"MONTH:"`），以及 `ReportFactory` 裡編碼成 `BirthDataGrain?`
 * （`MONTH` 即 `null`）。兩者都無法序列化。
 *
 * 對 [AbstractEvent] 的子型別**不要**再寫窮舉 `when`，改問這裡的能力屬性 ——
 * 如此新增事件型別（例如帶起訖的期間事件）時，只需讓它回答自己的 grain，
 * 既有的判讀點一處都不必改。這與 [BirthDataGrain] 的 `includeXxx` 是同一個模式。
 */
enum class EventGrain {
  /** 只知年月。排不出日盤，僅參與事件分群的月級掃描。 */
  MONTH,

  /** 知道日期、不知時刻。錨定於正午，其 ASC/MC 恆為捏造。 */
  DAY,

  /** 時刻精確，事件盤的 ASC/MC 有效。 */
  MINUTE
}

/**
 * 事件是否精細到足以展開**日級行運**（含事件當下的 synastry 快照）。
 *
 * [EventGrain.MONTH] 為 false 不是缺陷而是正確行為：連日期都沒有，
 * 硬排一張盤只會產出看似有據的噪音。但這個「跳過」必須是**顯式且可序列化**的 ——
 * 餵給 LLM 的素材若只是少了一筆 synastry，LLM 分不出是「沒事發生」還是「我們沒算」。
 */
val EventGrain.canDayLevelTransit: Boolean
  get() = this != EventGrain.MONTH

/**
 * 事件盤（外盤）自身的時刻精度。[EventGrain.MONTH] 排不出盤，故無對應。
 *
 * 這是「事件資料模型」與「盤」兩層唯一的交會點，刻意只留這一處。
 * 之所以映射到 [BirthDataGrain] 而非另立 `ChartGrain`：非本命盤實際只消費
 * [includeAxis] 一個屬性（見 `IHoroscopeFeature.synastry` 的 `outerGrain`），
 * `includeProfection` / `includeLunarReturns` / [includeLunarPosition] 與 [DayNightSource]
 * 全是本命獨有的技法。型別超載的代價目前僅止於命名，不值得為此改動數十處呼叫點。
 */
val EventGrain.chartGrain: BirthDataGrain?
  get() = when (this) {
    EventGrain.MINUTE -> BirthDataGrain.MINUTE
    EventGrain.DAY    -> BirthDataGrain.DAY
    EventGrain.MONTH  -> null
  }
