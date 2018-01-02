/**
 * @author smallufo
 * Created on 2008/6/30 at 上午 3:28:14
 */
package destiny.astrology

import java.io.Serializable
import java.util.*

/**
 * 對不同的 Point 做排序的動作 , 優先權： 行星 , 交點 , 小行星 , 恆星 , 漢堡
 */
class PointComparator : Comparator<Point>, Serializable {

  private val starClasses = arrayOf(Planet::class.java, LunarNode::class.java, Asteroid::class.java, FixedStar::class.java, Hamburger::class.java)

  override fun compare(p1: Point, p2: Point): Int {
    if (p1 is Planet && p2 is Planet ) {
      return p1.hashCode() - p2.hashCode()
    }

    val p1class = p1.javaClass
    val p2class = p2.javaClass
    return if (p1class == p2class) {
      p1.hashCode() - p2.hashCode()
    } else {
      val starClassesList = Arrays.asList(*starClasses)
      val index1 = starClassesList.indexOf(p1class)
      val index2 = starClassesList.indexOf(p2class)
      index1 - index2
      //starClassesList.indexOf(p1class) - starClassesList.indexOf(p2class)
    }
  }

}
