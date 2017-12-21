/**
 * Created by smallufo on 2017-12-21.
 */
package destiny.astrology.classical

import destiny.astrology.DayNight
import destiny.astrology.Planet
import destiny.astrology.Point
import destiny.astrology.ZodiacSign
import destiny.astrology.classical.Dignity.RULER

/** Ruler , +5 */
interface IRuler {

  /** 不分日夜，取得 RULER , 傳回的為 非null值 */
  fun getRuler(sign: ZodiacSign): Planet {
    return getRuler(sign, null)!!
  }

  /** @param dayNight 若有傳值，取得「日夜區分版本」的 [RULER] (nullable), 否則取得一般版本的 [RULER] (非null) */
  fun getRuler(sign: ZodiacSign, dayNight: DayNight?): Planet?

  /** 不分日夜，取得此行星為哪個星座的主人 , 傳回的為 非null值 . size 固定為 2  */
  fun getRuling(planet: Planet): Set<ZodiacSign>

  /**
   * 取得此行星在日、夜 是什麼星座的 [RULER]
   * @param dayNight 若為 null 則取得不分日夜的版本(non-null)
   */
  fun getRuling(planet: Planet, dayNight: DayNight): ZodiacSign?
}

/** Detriment , -5 */
interface IDetriment {

  /** 在此星座 陷(-5) 的行星為何 */
  fun getDetriment(sign: ZodiacSign): Planet

  /** 此行星在哪些星座 陷 (-5) */
  fun getDetriment(planet: Planet): Set<ZodiacSign>

  /** 在此星座 ，區分日夜， 陷(-5) 的行星為何?  (好像沒看過 Detriment 還區分日夜) */
  fun getDetriment(sign: ZodiacSign, dayNight: DayNight?): Planet?

}


/** Exaltation , +4 */
interface IExaltation {

  /** 哪顆星體在此星座 擢升 (EXALT , +4) , 必定為 1 or 0 顆星 */
  fun getExaltation(sign: ZodiacSign): Point?

  /** 此星體在哪個星座 擢升 (EXALT , +4) , 前者逆函數 */
  fun getExaltation(point: Point) : ZodiacSign?

}