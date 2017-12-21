/**
 * @author smallufo
 * Created on 2007/11/26 at 上午 5:33:26
 */
package destiny.astrology.classical

import destiny.astrology.Point
import destiny.astrology.ZodiacSign
import destiny.astrology.classical.Dignity.RULER

/**
 * 取得 行星 ( Planet ) 及 南北交點 ( Node ) 在星座 ( ZodiacSign ) 的 : 旺 Rulership , 廟 Exaltation , 陷 Detriment , 落 Fail <br></br>
 * 內定實作為 托勒密表格 EssentialDefaultImpl <br></br>
 * REDF 分別為 Rulership / Exaltation / Detriment / Fall 的第一個字母所組成
 */
internal interface IEssentialRedf : IRuler , IDetriment {

  /** 取得黃道帶上某星座，其 Dignity 之 廟旺陷落 各是何星  */
  fun getPoint(sign: ZodiacSign, dignity: Dignity): Point?

  /** @param dayNight 若有傳值，取得「日夜區分版本」的 [RULER] (nullable), 否則取得一般版本的 [RULER] (非null) */
  //override fun getRuler(sign: ZodiacSign, dayNight: DayNight?): Planet?

  //fun getRuling(planet: Planet, dayNight: DayNight) : ZodiacSign?
}
