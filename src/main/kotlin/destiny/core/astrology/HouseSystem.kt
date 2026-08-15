/**
 * @author smallufo
 * Created on 2007/5/29 at 上午 2:29:09
 */
package destiny.core.astrology

import destiny.tools.ILocaleString
import java.util.*
import destiny.tools.getTitle
import destiny.tools.toLang
import destiny.tools.Lang

/**
 * 名稱查表走全站慣例（`HouseSystem.properties` 的 `<常數>.title`），
 * 而非早期與 [Ayanamsa] 共用 `Astrology.properties` 的 `HouseSystem.<常數>`。
 *
 * 改動原因（2026-08-15）：舊慣例讓 `HouseSystem.PLACIDUS.getTitle(lang)` ——
 * 也就是每個人都會先試的那條路 —— **靜默退回列舉名**，寫新程式的人不會發現自己走錯。
 * 現在兩條路徑同源，本函式只是保留給既有呼叫端的別名。
 */
fun HouseSystem.asLocaleString() = object : ILocaleString {
  override fun getTitle(lang: Lang): String = this@asLocaleString.getTitle(lang)
}

fun HouseSystem.toString(lang: Lang): String {
  return this.getTitle(lang)
}

/** 橋接，說明見 [destiny.tools.ILocaleString] */
fun HouseSystem.toString(locale: Locale): String = toString(locale.toLang())

/**
 * 分宮法 , Zodiac House Systems
 *
 * @param nameKey **已不是名稱查表的 key**（2026-08-15 起）。名稱走 `HouseSystem.properties`
 *   的 `<常數>.title`，也就是全站慣例的 [destiny.tools.getTitle]；本欄位僅為既有 API 保留。
 */
enum class HouseSystem(val nameKey: String) {

  PLACIDUS("HouseSystem.PLACIDUS"),
  KOCH("HouseSystem.KOCH"),
  /** 東昇/天頂 度數 均等三等分 , Porphyry */
  PORPHYRIUS("HouseSystem.PORPHYRIUS"),
  REGIOMONTANUS("HouseSystem.REGIOMONTANUS"),
  CAMPANUS("HouseSystem.CAMPANUS"),
  EQUAL("HouseSystem.EQUAL"),
  WHOLE_SIGN("HouseSystem.WHOLE_SIGN"), // 整宮制
  VEHLOW_EQUAL("HouseSystem.VEHLOW_EQUAL"),
  AXIAL_ROTATION("HouseSystem.AXIAL_ROTATION"),
  HORIZONTAL("HouseSystem.HORIZONTAL"),
  ALCABITIUS("HouseSystem.ALCABITIUS"),
  /**
   * axial rotation system/ Meridian houses
   * this is a non-quadrant system that can be described as both space- and time-based
   *
   * (wiki) Each house is exactly 2 sidereal hours long.
   *  */
  MERIDIAN("HouseSystem.MERIDIAN")
  ;
}
