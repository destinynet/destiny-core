/**
 * Created by smallufo on 2026-11-20.
 */
package destiny.core.astrology.geo

import destiny.core.astrology.AstroPoint
import destiny.core.astrology.IHoroscopeModel
import destiny.core.astrology.Planet
import destiny.core.astrology.classical.IRuler
import destiny.core.astrology.classical.RulerPtolemyImpl

/**
 * 一個主題（事業、愛情…）在地點占星裡看什麼：主題的宮與自然主星。
 *
 * 宮只有四個角宮對得上角線（1 → ASC、4 → IC、7 → DESC、10 → MC）；其餘的宮（2、5、6…）沒有自己的線，
 * 只透過**宮主星**進來 —— 例如愛情看 5、7 宮：DESC 線加重，5 宮主星與 7 宮主星的線也加重。
 */
data class GeoTopicFocus(val houses: List<Int>, val planets: Collection<Planet>) {

  /** 主題的宮裡是角宮的，對應的角 */
  val angles: Set<GeoAngle>
    get() = houses.mapNotNull { HOUSE_ANGLES[it] }.toSet()

  /** 主題要加重的星：自然主星＋主題各宮的宮主星（依 [natal] 的宮頭星座與 [ruler]） */
  fun stars(natal: IHoroscopeModel, ruler: IRuler = RulerPtolemyImpl): Set<AstroPoint> =
    planets.toSet() + houses.mapNotNull { h -> with(ruler) { natal.getCuspDegree(h).sign.getRulerPoint() } }

  companion object {
    val HOUSE_ANGLES: Map<Int, GeoAngle> = mapOf(1 to GeoAngle.ASC, 4 to GeoAngle.IC, 7 to GeoAngle.DESC, 10 to GeoAngle.MC)
  }
}

/**
 * 依主題調整的計分組態：主題的角 × [angleBoost]、主題的星 × [starBoost]，其餘不變。
 *
 * 只動強度軸（權重），不動吉凶軸（[RelocationScoreConfig.starValences]）：
 * 「事業」不會讓土星變吉，只會讓土星壓 MC 這件事更顯著 —— 排名鍵仍是 suitability，凶星線照樣把地點壓下去。
 * 主題的星不在 [RelocationScoreConfig.starWeights] 內時（例如三王星被拿掉了）不新增。
 */
fun RelocationScoreConfig.focusedOn(
  focus: GeoTopicFocus,
  natal: IHoroscopeModel,
  ruler: IRuler = RulerPtolemyImpl,
  angleBoost: Double = 1.5,
  starBoost: Double = 1.5,
): RelocationScoreConfig {
  require(angleBoost >= 1.0 && starBoost >= 1.0) { "boost must be >= 1" }
  val angles = focus.angles
  val stars = focus.stars(natal, ruler)
  return copy(
    angleWeights = angleWeights.mapValues { (a, w) -> if (a in angles) w * angleBoost else w },
    starWeights = starWeights.mapValues { (p, w) -> if (p in stars) w * starBoost else w },
  )
}
