/**
 * Created by smallufo on 2017-12-21.
 */
package destiny.astrology.classical

import destiny.astrology.DayNight
import destiny.astrology.DayNight.DAY
import destiny.astrology.DayNight.NIGHT
import destiny.astrology.Planet
import destiny.astrology.ZodiacSign
import destiny.astrology.ZodiacSign.*
import java.io.Serializable



/**
 * Ruler 托勒密 Ptolomy 實作
 */
class RulerPtolomyImpl : AbstractPtolomy() , IRuler, Serializable {

  override fun getRuler(sign: ZodiacSign, dayNight: DayNight?): Planet? {
    return if (dayNight != null)
      rulerDayNightMap[(sign to dayNight)]
    else
      rulerMap[(sign to Dignity.RULER)]
  }

  override fun getRuling(planet: Planet, dayNight: DayNight): ZodiacSign? {
    return rulingDayNightMap[(planet to dayNight)]
  }

  /** 不分日夜，取得此行星為哪兩個星座的 ruler (日月除外 , 各只有一個星座） */
  override fun getRuling(planet: Planet): Set<ZodiacSign> {
    return rulingMap[planet]!!
  }
}


/**
 * 陷 , -5
 * Detriment 托勒密 Ptolomy 實作
 * (Ruler 對沖)
 */
class DetrimentPtolomyImpl : AbstractPtolomy() , IDetriment , Serializable {

  override fun getDetriment(sign: ZodiacSign): Planet {
    return rulerMap[sign.oppositeSign to Dignity.RULER]!!
  }

  override fun getDetriment(sign: ZodiacSign, dayNight: DayNight?): Planet? {
    return rulerDayNightMap[sign.oppositeSign to dayNight]
  }

}

/**
 * 托勒密 RULER / DETRIMENT 共用表格
 */
abstract class AbstractPtolomy {

  companion object {
    /** 不考量「日、夜」的 ruler */
    internal val rulerMap = mapOf<Pair<ZodiacSign, Dignity>, Planet>(
      (ARIES to Dignity.RULER) to Planet.MARS,
      (TAURUS to Dignity.RULER) to Planet.VENUS,
      (GEMINI to Dignity.RULER) to Planet.MERCURY,
      (CANCER to Dignity.RULER) to Planet.MOON,
      (LEO to Dignity.RULER) to Planet.SUN,
      (VIRGO to Dignity.RULER) to Planet.MERCURY,
      (LIBRA to Dignity.RULER) to Planet.VENUS,
      (SCORPIO to Dignity.RULER) to Planet.MARS,
      (SAGITTARIUS to Dignity.RULER) to Planet.JUPITER,
      (CAPRICORN to Dignity.RULER) to Planet.SATURN,
      (AQUARIUS to Dignity.RULER) to Planet.SATURN,
      (PISCES to Dignity.RULER) to Planet.JUPITER
    )

    /** 不考量日、夜的 ruling map */
    internal val rulingMap: Map<Planet, Set<ZodiacSign>> =
      rulerMap.entries
        .map { (sign_to_RULER , planet) -> planet to sign_to_RULER.first }
        .groupBy { (planet , sign) -> planet }
        .mapValues { (planet ,planet_to_sign) ->  planet_to_sign.map { p: Pair<Planet, ZodiacSign> -> p.second } }
        .mapValues { it.value.toSet() }

    /**
     * 考量日夜的 rulerMap , 參考表格 : https://imgur.com/a/bZ6ij
     * 讀作 : 什麼星座的日/夜 的 ruler 是誰(maybe null)
     * */
    internal val rulerDayNightMap = mapOf<Pair<ZodiacSign, DayNight>, Planet>(
      (ARIES to DAY) to Planet.MARS,
      (TAURUS to NIGHT) to Planet.VENUS,
      (GEMINI to DAY) to Planet.MERCURY,
      (CANCER to DAY) to Planet.MOON,
      (CANCER to NIGHT) to Planet.MOON,
      (LEO to DAY) to Planet.SUN,
      (LEO to NIGHT) to Planet.SUN,
      (VIRGO to NIGHT) to Planet.MERCURY,
      (LIBRA to DAY) to Planet.VENUS,
      (SCORPIO to NIGHT) to Planet.MARS,
      (SAGITTARIUS to DAY) to Planet.JUPITER,
      (CAPRICORN to NIGHT) to Planet.SATURN,
      (AQUARIUS to DAY) to Planet.SATURN,
      (PISCES to NIGHT) to Planet.JUPITER
    )

    /**
     * (火星, DAY) ==> 牡羊
     * (火星, NIGHT) ==> 天蠍
     *
     * (金星, NIGHT) ==> 金牛
     * (金星, DAY) ==> 天秤
     *
     * (水星, DAY) ==> 雙子
     * (水星, NIGHT) ==> 處女
     *
     * (月亮, DAY) ==> 巨蟹
     * (月亮, NIGHT) ==> 巨蟹
     *
     * (太陽, DAY) ==> 獅子
     * (太陽, NIGHT) ==> 獅子
     *
     * (木星, DAY) ==> 射手
     * (木星, NIGHT) ==> 雙魚
     *
     * (土星, NIGHT) ==> 摩羯
     * (土星, DAY) ==> 水瓶
     */
    internal val rulingDayNightMap: Map<Pair<Planet, DayNight>, ZodiacSign> =
      rulerDayNightMap
      .entries
      .map { (sign_to_DN, planet) -> planet to sign_to_DN }
      .groupBy { (planet, sign_to_DN) -> planet }
      .flatMap { (planet, sign_to_DN) -> sign_to_DN }
      .map { (planet, sign_to_DN) -> (planet to sign_to_DN.second) to sign_to_DN.first }
      .toMap()
  }
}
