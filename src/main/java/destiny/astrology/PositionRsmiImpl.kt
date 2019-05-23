/**
 * Created by smallufo on 2019-05-23.
 */
package destiny.astrology

import destiny.core.calendar.ILocation

open class PositionRsmiImpl(private val rsmi: Rsmi) : AbstractPositionImpl<Rsmi>(rsmi) {

  override fun getPosition(gmtJulDay: Double, loc: ILocation, centric: Centric, coordinate: Coordinate, starPositionImpl: IStarPosition<*>, houseCuspImpl: IHouseCusp): IPos {
    return houseCuspImpl.getHouseCuspMap(gmtJulDay , loc , HouseSystem.PLACIDUS , Coordinate.ECLIPTIC).let { map: Map<Int, Double> ->
      when(rsmi) {
        Rsmi.RISING -> Pos(map.getValue(1) , 0.0)
        Rsmi.SETTING-> Pos(map.getValue(7) , 0.0)
        Rsmi.MERIDIAN -> Pos(map.getValue(10) , 0.0)
        Rsmi.NADIR -> Pos(map.getValue(4) , 0.0)
      }
    }
  }
}