/**
 * @author smallufo
 * Created on 2007/5/21 at 上午 5:46:51
 */
package destiny.core.astrology

import java.io.Serializable


interface IAzimuth {
  /** 地平方位角 , 以北為 0度，東為90度，南為 180度，西為 270度  */
  val azimuthDeg: Double
  /** 真實高度  */
  val trueAltitude: Double
  /** 視高度 (會考量到大氣折射)  */
  val apparentAltitude: Double
}

/**
 * 地平方位角 , 地平座標系統 (Horizontal Coordinate System)
 */
data class Azimuth(
  /** 地平方位角 , 以北為 0度，東為90度，南為 180度，西為 270度  */
  override val azimuthDeg: Double,
  /** 真實高度  */
  override val trueAltitude: Double,
  /** 視高度 (會考量到大氣折射)  */
  override val apparentAltitude: Double) : IAzimuth , Serializable
