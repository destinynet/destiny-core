package destiny.core.astrology

import destiny.core.DayNight

/**
 * 晝夜生的**來源**—— 取代舊的 `BirthDataGrain.dayNight: DayNight?` 加 `includeFirdaria: Boolean`。
 *
 * ## 為什麼需要一個型別
 *
 * 舊設計把「晝夜」直接掛在 grain 上，`null` 只有一種意思：「去看盤面太陽在第幾宮」。
 * 這對 [BirthDataGrain.MINUTE] 成立，對 [BirthDataGrain.DAY] 永遠碰不到（`includeFirdaria` 先擋掉）。
 *
 * [BirthDataGrain.HOUR2] 一進來，`null` 就承載了第二種意思，而且是致命的那種。
 * 以 2026-08-18 台灣酉時為例：儲存的中點是 17:57，盤面太陽在地平線上 → 判白天；
 * 但酉時涵蓋 16:57–18:57，日落在 18:25，真實出生若落在 18:25 之後其實是**夜生**。
 * 代價不是誤差 —— 晝生從太陽起、夜生從月亮起，Firdaria 主運序列整組換掉。
 *
 * 比表面更糟的是，盤面推導的實作是 `getHouse(SUN) in 7..12` —— 宮位需要 ASC，
 * 而 **ASC 正是 [BirthDataGrain.HOUR2] 唯一不能信的東西**（±15°）。
 * 所以時辰級走盤面推導是雙重不可靠：既把捏造的中點當真，又拿捏造的宮首去判。
 *
 * ## HOUR2 的正確判定
 *
 * 用 `IDayNight`（太陽**視高度** ≥ 0，不經宮位）測該時辰的兩個端點：兩端一致才採信，
 * 不一致代表橫跨日出或日落 → [Indeterminate]。
 *
 * 這在台灣自然收斂成「卯酉不可判」（日出真太陽時約 5:07~6:40、日落約 17:20~18:53，
 * 整段落在卯／酉之內），**不必硬編碼**；高緯度會自動位移到寅時或辰時，
 * 極區永晝／永夜時所有時辰都可判 —— 硬編碼卯酉版在後兩種情況都是錯的。
 */
sealed interface DayNightSource {

  /** 已知晝夜：[BirthDataGrain.DayNightOnly]，或 [BirthDataGrain.HOUR2] 判定成功。 */
  data class Known(val value: DayNight) : DayNightSource

  /** [BirthDataGrain.MINUTE]：由盤面太陽位置推導。 */
  data object FromChart : DayNightSource

  /**
   * [BirthDataGrain.HOUR2] 橫跨日出／日落 —— **知道自己不知道**。
   *
   * 與 [Unavailable] 在 Firdaria 上行為相同（都略過），但語意不同，刻意不合併：
   * 前者是「這個人的時辰跨越了日落」，後者是「我們根本不知道時辰」。
   * 要對使用者解釋為什麼沒有法達時，這兩句話不一樣。
   */
  data object Indeterminate : DayNightSource

  /** [BirthDataGrain.DAY]：連晝夜都不知道。 */
  data object Unavailable : DayNightSource
}

/**
 * 由 grain 決定晝夜來源。[BirthDataGrain.HOUR2] 需要 `IHour` + `IDayNight`
 * （皆在 destiny-core-impl），故以 [hour2Resolver] 由呼叫端注入；
 * 其餘四種純由型別決定，不會觸發 [hour2Resolver]。
 */
inline fun BirthDataGrain.resolveDayNightSource(hour2Resolver: () -> DayNightSource): DayNightSource = when (this) {
  BirthDataGrain.DAY             -> DayNightSource.Unavailable
  is BirthDataGrain.DayNightOnly -> DayNightSource.Known(value)
  BirthDataGrain.HOUR2           -> hour2Resolver()
  BirthDataGrain.MINUTE          -> DayNightSource.FromChart
}
