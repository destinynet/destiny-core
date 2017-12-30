/**
 * @author smallufo
 * Created on 2008/1/16 at 下午 8:51:47
 */
package destiny.astrology

import java.util.*

class DegreeComparator(private val horoscope: Horoscope) : Comparator<Point> {

  override fun compare(p1: Point, p2: Point): Int {

    val pos1: PositionWithAzimuth? = horoscope.getPosition(p1)
    val pos2: PositionWithAzimuth? = horoscope.getPosition(p2)

    return if (pos1 != null && pos2 != null)
      (pos1.lng - pos2.lng).toInt()
    else
      0
  }

}
