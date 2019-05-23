/**
 * Created by smallufo on 2019-05-23.
 */
package destiny.astrology

import destiny.core.calendar.ILocation

open class PositionRsmiImpl(private val rsmi: Rsmi ,
                            private val azimuthImpl: IAzimuthCalculator) : AbstractPositionImpl<Rsmi>(rsmi) {

  override fun getPosition(gmtJulDay: Double, loc: ILocation, centric: Centric, coordinate: Coordinate, starPositionImpl: IStarPosition<*>, houseCuspImpl: IHouseCusp): IPos {
    return houseCuspImpl.getHouseCuspMap(gmtJulDay , loc , HouseSystem.PLACIDUS , coordinate).let { map: Map<Int, Double> ->
      val pos = when(rsmi) {
        Rsmi.RISING -> Pos(map.getValue(1) , 0.0)
        Rsmi.SETTING-> Pos(map.getValue(7) , 0.0)
        Rsmi.MERIDIAN -> Pos(map.getValue(10) , 0.0)
        Rsmi.NADIR -> Pos(map.getValue(4) , 0.0)
      }

      val azimuth: Azimuth = when (coordinate) {
        /** TODO : 恆星座標系統 [Coordinate.SIDEREAL]，「暫時」使用黃道座標系統計算 */
        Coordinate.ECLIPTIC , Coordinate.SIDEREAL -> azimuthImpl.getAzimuthFromEcliptic(pos , gmtJulDay , loc )
        Coordinate.EQUATORIAL -> azimuthImpl.getAzimuthFromEquator(pos , gmtJulDay , loc)
      }

      PosWithAzimuth(pos , azimuth)
    }
  }
}