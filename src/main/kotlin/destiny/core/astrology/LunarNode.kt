/**
 * @author smallufo
 * Created on 2007/12/23 at 上午 5:25:44
 */
package destiny.core.astrology

import destiny.core.IPoints
import destiny.core.News
import destiny.core.News.NorthSouth.NORTH
import destiny.core.News.NorthSouth.SOUTH
import destiny.core.Point
import destiny.core.astrology.NodeType.MEAN
import destiny.core.astrology.NodeType.TRUE
import destiny.core.toString
import java.util.*
import kotlin.reflect.KClass

sealed class LunarNode(nameKey: String,
                       abbrKey: String,
                       val northSouth: News.NorthSouth,
                       val nodeType: NodeType,
                       unicode: Char) :
  LunarPoint(nameKey, abbrKey, Star::class.java.name , unicode), Comparable<LunarNode> {

  /**
   * 真實北交點，計算方法，以下兩者結果相同
   * ApsisImpl.getPosition(Planet.MOON , Apsis.ASCENDING , gmt, Coordinate.ECLIPTIC , NodeType.TRUE);
   * StarPositionImpl.getPosition(LunarNode.NORTH_TRUE, gmt);
   */
  object NORTH_TRUE : LunarNode("LunarNode.NORTH", "LunarNode.NORTH_ABBR", NORTH, TRUE, '☊')

  /**
   * 平均北交點，計算方法，以下兩者結果相同
   * ApsisImpl.getPosition(Planet.MOON , Apsis.ASCENDING , gmt, Coordinate.ECLIPTIC , NodeType.MEAN);
   * StarPositionImpl.getPosition(LunarNode.NORTH_MEAN, gmt);
   */
  object NORTH_MEAN : LunarNode("LunarNode.NORTH", "LunarNode.NORTH_ABBR", NORTH, MEAN, '☊')

  /**
   * 真實南交點，計算方法，以下兩者結果相同
   * ApsisImpl.getPosition(Planet.MOON , Apsis.DESCENDING , gmt, Coordinate.ECLIPTIC , NodeType.TRUE);
   * StarPositionImpl.getPosition(LunarNode.SOUTH_TRUE, gmt);
   */
  object SOUTH_TRUE : LunarNode("LunarNode.SOUTH", "LunarNode.SOUTH_ABBR", SOUTH, TRUE, '☋')

  /**
   * 平均南交點，計算方法，以下兩者結果相同
   * ApsisImpl.getPosition(Planet.MOON , Apsis.DESCENDING , gmt, Coordinate.ECLIPTIC , NodeType.MEAN);
   * StarPositionImpl.getPosition(LunarNode.SOUTH_MEAN, gmt);
   */
  object SOUTH_MEAN : LunarNode("LunarNode.SOUTH", "LunarNode.SOUTH_ABBR", SOUTH, MEAN, '☋')



  override fun compareTo(other: LunarNode): Int {
    if (this == other)
      return 0

    return inner_values.indexOf(this) - inner_values.indexOf(other)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is LunarNode) return false
    if (!super.equals(other)) return false

    if (northSouth != other.northSouth) return false
    if (nodeType != other.nodeType) return false

    return true
  }

  override fun hashCode(): Int {
    var result = super.hashCode()
    result = 31 * result + northSouth.hashCode()
    result = 31 * result + nodeType.hashCode()
    return result
  }

  companion object : IPoints<LunarNode> {

    override val type: KClass<out Point> = LunarNode::class

    private val inner_values by lazy {
      arrayOf(NORTH_TRUE, NORTH_MEAN, SOUTH_TRUE, SOUTH_MEAN)
    }
    val trueArray by lazy { arrayOf(NORTH_TRUE, SOUTH_TRUE) }
    val meanArray by lazy { arrayOf(NORTH_MEAN, SOUTH_MEAN) }
    val trueList by lazy { listOf(*trueArray) }
    val meanList by lazy { listOf(*meanArray) }

    override val values: Array<LunarNode> by lazy { trueArray }

    override fun fromString(value: String, locale: Locale): LunarNode? {
      return values.firstOrNull {
        it.toString(locale).equals(value, ignoreCase = true)
      }
    }


    fun of(northSouth: News.NorthSouth, nodeType: NodeType): LunarNode {
      return inner_values.first {
        it.northSouth == northSouth && it.nodeType === nodeType
      }
    }
  }
}
