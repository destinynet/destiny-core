/**
 * Created by smallufo on 2026-11-14.
 */
package destiny.core.astrology.classical

import destiny.core.DayNight
import destiny.core.astrology.Planet
import destiny.core.astrology.ZodiacSign

/**
 * 現代占星的守護星：天蠍＝冥王星、水瓶＝天王星、雙魚＝海王星，其餘九個星座與托勒密（[RulerPtolemyImpl]）相同。
 *
 * 火星、土星、木星因此各只守護一個星座（牡羊、摩羯、射手）。
 * 現代守護不分日夜：帶 `dayNight` 的版本在這三個星座一律回現代的那一顆，其餘照托勒密的日夜版本
 */
object RulerModernImpl : IRuler {

  /** 與托勒密不同的三個星座 */
  val modernRulers: Map<ZodiacSign, Planet> =
    mapOf(ZodiacSign.SCORPIO to Planet.PLUTO, ZodiacSign.AQUARIUS to Planet.URANUS, ZodiacSign.PISCES to Planet.NEPTUNE)

  override fun ZodiacSign.getRulerPoint(dayNight: DayNight?): Planet? =
    modernRulers[this] ?: with(RulerPtolemyImpl) { getRulerPoint(dayNight) }

  override fun Planet.getRulingSigns(): Set<ZodiacSign> {
    val own = modernRulers.filterValues { it === this }.keys
    return if (own.isNotEmpty()) own
    else Planet.classicalList.takeIf { this in it }?.let { with(RulerPtolemyImpl) { getRulingSigns() } - modernRulers.keys } ?: emptySet()
  }

  override fun Planet.getRulingSign(dayNight: DayNight): ZodiacSign? =
    modernRulers.entries.firstOrNull { it.value === this }?.key
      ?: Planet.classicalList.takeIf { this in it }?.let { with(RulerPtolemyImpl) { getRulingSign(dayNight) } }?.takeIf { it !in modernRulers }
}
