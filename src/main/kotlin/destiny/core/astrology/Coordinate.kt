/*
 * @author smallufo
 * @date 2004/10/23
 * @time 上午 04:24:30
 */
package destiny.core.astrology

/**
 * Zodiac selection <br></br>
 * 黃道(ECLIPTIC) / 赤道(TROPICAL) / 恆星(SIDEREAL) 座標系 參數
 */
enum class Coordinate(val nameKey: String) {

  /** 黃道座標系  */
  ECLIPTIC("Coordinate.ECLIPTIC"),
  /** 赤道座標系  */
  EQUATORIAL("Coordinate.EQUATORIAL"),
  /** 恆星座標系  */
  SIDEREAL("Coordinate.SIDEREAL");
}
