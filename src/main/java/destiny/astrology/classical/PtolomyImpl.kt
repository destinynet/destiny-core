/**
 * Created by smallufo on 2017-12-21.
 */
package destiny.astrology.classical

import destiny.astrology.*
import destiny.astrology.DayNight.DAY
import destiny.astrology.DayNight.NIGHT
import destiny.astrology.Planet.*
import destiny.astrology.ZodiacSign.*
import java.io.Serializable


/**
 * Ruler 托勒密 Ptolemy 實作
 */
class RulerPtolemyImpl : AbstractPtolemy(), IRuler, Serializable {

  override fun getRuler(sign: ZodiacSign, dayNight: DayNight?): Planet? {
    return if (dayNight != null)
      rulerDayNightMap[(sign to dayNight)]
    else
      rulerMap[sign]
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
 * Detriment 托勒密 Ptolemy 實作
 * (Ruler 對沖)
 */
class DetrimentPtolemyImpl : AbstractPtolemy(), IDetriment, Serializable {

  override fun getDetriment(sign: ZodiacSign): Planet {
    return rulerMap[sign.oppositeSign]!!
  }

  /** 此行星在哪些星座 陷 (-5), 至少一個，最多兩個 */
  override fun getDetriment(planet: Planet): Set<ZodiacSign> {
    return detrimentMap[planet]!! // 托勒密表格，必定有值
  }

  override fun getDetriment(sign: ZodiacSign, dayNight: DayNight?): Planet? {
    return rulerDayNightMap[sign.oppositeSign to dayNight]
  }
}


class ExaltationPtolemyImpl : AbstractPtolemy(), IExaltation, Serializable {

  /** 哪顆星體在此星座 擢升 (EXALT , +4) , 必定為 1 or 0 顆星 */
  override fun getExaltation(sign: ZodiacSign): Point? {
    return findPoint(sign, exaltDegreeMap)
  }

  /** 此星體在哪個星座 擢升 (EXALT , +4) , 前者逆函數 , 必定有值 */
  override fun getExaltation(point: Point): ZodiacSign? {
    return exaltSignMap[point]
  }

}


/**
 * 托勒密 RULER / DETRIMENT 共用表格
 */
abstract class AbstractPtolemy {

  fun findPoint(sign: ZodiacSign, map: Map<Point, Double>): Point? {
    return map.entries
      .filter { (_, value) -> sign === getZodiacSign(value) }
      .map { entry -> entry.key }
      .firstOrNull()
  }

  companion object {
    /** 不考量「日、夜」的 ruler */
    internal val rulerMap = mapOf<ZodiacSign, Planet>(
      ARIES to MARS,
      TAURUS to VENUS,
      GEMINI to MERCURY,
      CANCER to MOON,
      LEO to SUN,
      VIRGO to MERCURY,
      LIBRA to VENUS,
      SCORPIO to MARS,
      SAGITTARIUS to JUPITER,
      CAPRICORN to SATURN,
      AQUARIUS to SATURN,
      PISCES to JUPITER
    )

    /** 不考量日、夜的 ruling map */
    internal val rulingMap: Map<Planet, Set<ZodiacSign>> = rulerMap
      .map { (sign, planet) -> planet to sign }
      .groupBy { (planet, sign) -> planet }
      .mapValues { (planet, v) -> v.map { p -> p.second } }
      .mapValues { it.value.toSet() }


    /** 此行星，在哪些星座 陷 (-5) */
    internal val detrimentMap: Map<Planet, Set<ZodiacSign>> = rulerMap
      .map { (sign, planet) -> planet to sign.oppositeSign }
      .groupBy { (planet, sign) -> planet }
      .mapValues { (planet, v) -> v.map { p -> p.second } }
      .mapValues { it.value.toSet() }

    /**
     * 考量日夜的 rulerMap , 參考表格 : https://imgur.com/a/bZ6ij
     * 讀作 : 什麼星座的日/夜 的 ruler 是誰(maybe null)
     * */
    internal val rulerDayNightMap = mapOf<Pair<ZodiacSign, DayNight>, Planet>(
      (ARIES to DAY) to MARS,
      (TAURUS to NIGHT) to VENUS,
      (GEMINI to DAY) to MERCURY,
      (CANCER to DAY) to MOON,
      (CANCER to NIGHT) to MOON,
      (LEO to DAY) to SUN,
      (LEO to NIGHT) to SUN,
      (VIRGO to NIGHT) to MERCURY,
      (LIBRA to DAY) to VENUS,
      (SCORPIO to NIGHT) to MARS,
      (SAGITTARIUS to DAY) to JUPITER,
      (CAPRICORN to NIGHT) to SATURN,
      (AQUARIUS to DAY) to SATURN,
      (PISCES to NIGHT) to JUPITER
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


    /** 存放星體在黃道帶上幾度得到 Exaltation (+4) 的度數  */
    internal val exaltDegreeMap = mapOf<Point, Double>(
      SUN to 19.0  // 太陽在戌宮 19度 exalted.
      , MOON to 33.0  // 月亮在酉宮 03度 exalted.
      , MERCURY to 165.0  // 水星在巳宮 15度 exalted.
      , VENUS to 357.0  // 金星在亥宮 27度 exalted.
      , MARS to 298.0  // 火星在丑宮 28度 exalted.
      , JUPITER to 105.0  // 木星在未宮 15度 exalted.
      , SATURN to 201.0  // 土星在辰宮 21度 exalted.
      , LunarNode.NORTH_TRUE to 63.0  //北交點在 申宮 03度 exalted.
      , LunarNode.NORTH_MEAN to 63.0  //北交點在 申宮 03度 exalted.
      , LunarNode.SOUTH_TRUE to 243.0  //南交點在 寅宮 03度 exalted.
      , LunarNode.SOUTH_MEAN to 243.0  //南交點在 寅宮 03度 exalted.
    )

    /** 承上，儲存的是星座值 */
    internal val exaltSignMap: Map<Point, ZodiacSign> = exaltDegreeMap
      .mapValues { (sign , degree) -> getZodiacSign(degree) }
      .toMap()
  }
}
