/**
 * @author smallufo
 * Created on 2008/6/30 at 上午 3:28:14
 */
package destiny.core.astrology

import java.io.Serializable

/**
 * 對不同的 Point 做排序的動作 , 優先權： 行星 , 交點 , 小行星 , 恆星 , 阿拉伯點 , 漢堡
 */
object AstroPointComparator : Comparator<AstroPoint>, Serializable {

  private val starClasses = arrayOf(
    Planet::class.java,
    Axis::class.java,
    LunarPoint::class.java,
    Asteroid::class.java,
    FixedStar::class.java,
    Arabic::class.java,
    Hamburger::class.java,
    LunarStation::class.java)

  override fun compare(p1: AstroPoint, p2: AstroPoint): Int {
    if (p1 is Planet && p2 is Planet) {
      return Planet.values.indexOf(p1) - Planet.values.indexOf(p2)
    }

    val p1class = p1.javaClass
    val p2class = p2.javaClass
    return if (p1class == p2class) {
      p1.hashCode() - p2.hashCode()
    } else {
      val index1 = starClasses.first { it.isInstance(p1) }.let { starClasses.indexOf(it) }
      val index2 = starClasses.first { it.isInstance(p2) }.let { starClasses.indexOf(it) }
      if (index1 != index2) {
        index1 - index2
      } else {
        p1.hashCode() - p2.hashCode()
      }
    }
  }

}
