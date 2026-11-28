/**
 * Created by smallufo on 2026-11-28.
 */
package destiny.core.fengshui

import destiny.core.iching.Symbol

/**
 * 八個方位（每個 45°）。[degree] 是該方位的中心，自北起順時針。
 *
 * 顯示名用 `getTitle(locale)`（訊息檔 `Direction8*.properties`）；不要在呼叫端另寫一張「坎 → 北」的對照表。
 */
enum class Direction8(val degree: Double) {
  N(0.0),
  NE(45.0),
  E(90.0),
  SE(135.0),
  S(180.0),
  SW(225.0),
  W(270.0),
  NW(315.0);

  companion object {
    /** 某個方位角（度，自北起順時針；不限 0–360）落在哪個方位：以各方位的中心 ±22.5° 分界 */
    fun of(degree: Double): Direction8 {
      val normalized = ((degree % 360.0) + 360.0) % 360.0
      return entries[(((normalized + 22.5) / 45.0).toInt()) % 8]
    }
  }
}

/** 後天八卦的方位：坎北、艮東北、震東、巽東南、離南、坤西南、兌西、乾西北 */
val Symbol.direction8: Direction8
  get() = when (this) {
    Symbol.坎 -> Direction8.N
    Symbol.艮 -> Direction8.NE
    Symbol.震 -> Direction8.E
    Symbol.巽 -> Direction8.SE
    Symbol.離 -> Direction8.S
    Symbol.坤 -> Direction8.SW
    Symbol.兌 -> Direction8.W
    Symbol.乾 -> Direction8.NW
  }
