/**
 * @author smallufo
 * Created on 2008/6/27 at 上午 4:34:57
 */
package destiny.astrology

import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import java.io.Serializable
import java.util.*

/** 存放星體交角的資料結構  */
class HoroscopeAspectData(p1: Point, p2: Point,
                          /** 兩星所形成的交角  */
                          val aspect: Aspect?,
                          /** orb 不列入 equals / hashCode 計算  */
                          val orb: Double) : Comparable<HoroscopeAspectData>, Serializable {
  private val pointComp = PointComparator()

  /** 存放形成交角的兩顆星體  */
  val twoPoints = Collections.synchronizedSet(TreeSet(pointComp))


  private val logger = LoggerFactory.getLogger(javaClass)

  init {
    twoPoints.add(p1)
    twoPoints.add(p2)
    if (twoPoints.size <= 1) {
      logger.warn("twoPoints size = {} , p1 = {} ({}) , p2 = {} ({}) . equals ? {}", twoPoints.size, p1, p1.hashCode(), p2, p2.hashCode(), p1 == p2)
    }
  }

  override fun toString(): String {

    return twoPoints.toString() + aspect!!.toString(Locale.TAIWAN) + " 誤差 " +
      StringUtils.substring(orb.toString(), 0, 4) + " 度"
  }



  /** 傳入一個 point , 取得另一個 point , 如果沒有，則傳回 null  */
  fun getAnotherPoint(thisPoint: Point): Point? {
    val itp = twoPoints.iterator()
    while (itp.hasNext()) {
      val p = itp.next()
      return if (p == thisPoint)
        itp.next()
      else
        p
    }
    return null
  }

  override fun hashCode(): Int {
    val prime = 31
    var result = 1
    result = prime * result + (aspect?.hashCode() ?: 0)
    result = prime * result + (twoPoints?.hashCode() ?: 0)
    return result
  }

  override fun equals(obj: Any?): Boolean {
    if (this === obj)
      return true
    if (obj == null)
      return false
    if (javaClass != obj.javaClass)
      return false
    val other = obj as HoroscopeAspectData?
    if (aspect == null) {
      if (other!!.aspect != null)
        return false
    } else if (aspect != other!!.aspect)
      return false
    if (twoPoints == null) {
      if (other.twoPoints != null)
        return false
    } else if (twoPoints != other.twoPoints)
      return false
    return true
  }

  override fun compareTo(o: HoroscopeAspectData): Int {
    val it1 = twoPoints.iterator()
    val it2 = o.twoPoints.iterator()
    val thisP0 = it1.next()
    val thisP1 = it1.next()
    val thatP0 = it2.next()
    val thatP1 = it2.next()

    return if (thisP0.javaClass.name == thatP0.javaClass.name && thisP0 == thatP0) {
      if (thisP1.javaClass.name == thatP1.javaClass.name)
        (thisP1 as Comparable<Point>).compareTo(thatP1)
      else
        pointComp.compare(thisP1, thatP1)
    } else {
      pointComp.compare(thisP0, thatP0)
    }

  }


}
