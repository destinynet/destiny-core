/**
 * Created by smallufo on 2026-11-03.
 */
package destiny.core.astrology.classical

import kotlin.reflect.KClass
import destiny.core.DayNight
import destiny.core.astrology.IHoroscopeModel
import destiny.core.astrology.Planet
import destiny.core.astrology.ZodiacDegree

/**
 * 顯現（phasis）：出生前後不久，[planet] 的運行出現轉折，這顆星的主題因此被強調。
 *
 * 四種轉折（[Type]）：
 * - [Type.STATION_RETROGRADE]：停滯轉逆（第一次停滯）
 * - [Type.STATION_DIRECT]：停滯轉順（第二次停滯）
 * - [Type.EMERGING]：離開太陽光束，重新看得見（偕日升，或內行星的昏星初見）
 * - [Type.SINKING]：進入太陽光束，從此看不見（偕日落）
 *
 * [days]：轉折的時刻減出生時刻（日）；負數 ＝ 出生前。[lngDeg]：轉折那一刻這顆星的黃經。
 * [oriental]：轉折那一刻在太陽的東方（先於太陽升起，晨星）為 true，西方（昏星）為 false。
 * [beams]：出入光束時判定用的光束半徑（與太陽的黃經距離，度）；停滯為 null —— 畫面照它畫光束，不另外寫死一份數字
 *
 * 吉凶由判斷層依流派決定，事實本身不帶評價：希臘化只說「顯著」；中世紀（Bonatti 一系）視轉逆如病之始、轉順如病之癒
 */
data class Phasis(
  val planet: Planet,
  val type: Type,
  val days: Double,
  val lngDeg: ZodiacDegree,
  val oriental: Boolean,
  val beams: Double? = null,
) : ClassicalFact {
  enum class Type { STATION_RETROGRADE, STATION_DIRECT, EMERGING, SINKING }

  val station: Boolean get() = type == Type.STATION_RETROGRADE || type == Type.STATION_DIRECT
}

/**
 * 顯現的判準（流派分歧見 [HELLENISTIC]／[MEDIEVAL]；實務只用一組）。
 *
 * - [days]：出生前後各幾天內的轉折才算（偕日升落一律用它；停滯在 [stationBy] 為 [StationBy.WINDOW] 時也用它）
 * - [types]：收哪幾種轉折
 * - [beams]：太陽光束的半徑（與太陽的黃經距離，度）—— 偕日升落就是穿過這條界線
 * - [stationBy]：停滯怎麼認定
 * - [speedRatio]：[StationBy.SPEED] 時，出生那一刻的速度不超過平均日速（[AverageDailyMotionMap]）的幾分之幾才算停滯
 * - [planets]：判哪些星（日月不會停滯；月亮每個月都出入光束，不列）
 */
data class PhasisOptions(
  val days: Double = 7.0,
  val types: Set<Phasis.Type> = Phasis.Type.entries.toSet(),
  val beams: Double = 15.0,
  val stationBy: StationBy = StationBy.WINDOW,
  val speedRatio: Double = 0.1,
  val planets: List<Planet> = listOf(Planet.MERCURY, Planet.VENUS, Planet.MARS, Planet.JUPITER, Planet.SATURN),
) {
  init {
    // 同一顆星兩次停滯至少相隔約 20 天（水星），窗開到 10 天以內，窗內至多一次停滯（實作依賴這一點）
    require(days > 0 && days <= 10) { "days 要在 (0, 10]：$days" }
  }

  enum class StationBy {
    /** 停滯的時刻落在出生前後 [days] 天內 */
    WINDOW,
    /** 出生那一刻的速度慢到幾乎不動（[speedRatio]）；[Phasis.days] 是最近一次停滯離出生幾天 */
    SPEED,
  }

  companion object {
    /** 希臘化（Paulus、Hephaistion 一系）：出生前後 7 天內，停滯或出入光束（光束 15°）都算顯現 */
    val HELLENISTIC = PhasisOptions()

    /**
     * 中世紀（Bonatti 一系）：只看停滯，以速度認定（出生時的速度不超過平均日速的十分之一）。
     * 傳本沒有給門檻的數字，十分之一是這裡取的值
     */
    val MEDIEVAL = PhasisOptions(
      types = setOf(Phasis.Type.STATION_RETROGRADE, Phasis.Type.STATION_DIRECT),
      stationBy = StationBy.SPEED,
    )
  }
}

/** 顯現。需要星曆（出生前後的位置與停滯），由實作的建構子取得 */
interface IPhasis : IClassicalFactSource {

  /** [planet] 在出生前後的轉折（依時間先後）；沒有就是空的 */
  fun getPhases(h: IHoroscopeModel, planet: Planet): List<Phasis>

  /** 全盤：依古典行星順序、再依時間先後 */
  fun getResults(h: IHoroscopeModel): List<Phasis>

  override fun getFacts(h: IHoroscopeModel, sect: DayNight?): List<ClassicalFact> = getResults(h)

  override val produces: Set<KClass<out ClassicalFact>> get() = setOf(Phasis::class)
}
