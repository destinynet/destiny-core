/**
 * Created by smallufo on 2019-05-23.
 */
package destiny.core.astrology

import destiny.core.calendar.ILocation

open class PositionRsmiImpl(private val axis: Axis,
                            private val azimuthImpl: IAzimuthCalculator,
                            private val houseCuspImpl: IHouseCusp) : AbstractPositionImpl<Axis>(axis) {

  override fun getPosition(gmtJulDay: Double, loc: ILocation, centric: Centric, coordinate: Coordinate, temperature: Double, pressure: Double): IPos {
    return houseCuspImpl.getHouseCuspMap(gmtJulDay , loc , HouseSystem.PLACIDUS , coordinate).let { map: Map<Int, ZodiacDegree> ->
      val pos = when(axis) {
        Axis.RISING -> Pos(map.getValue(1).value , 0.0)
        Axis.SETTING-> Pos(map.getValue(7).value , 0.0)
        Axis.MERIDIAN -> Pos(map.getValue(10).value , 0.0)
        Axis.NADIR -> Pos(map.getValue(4).value , 0.0)
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
