/**
 * @author smallufo
 * Created on 2007/12/23 at 上午 5:25:44
 */
package destiny.core.astrology

import destiny.core.IPoints
import destiny.core.News
import destiny.core.Point
import destiny.core.toString
import destiny.tools.serializers.astrology.LunarNodeSerializer
import kotlinx.serialization.Serializable
import java.util.*
import kotlin.reflect.KClass

@Serializable(with = LunarNodeSerializer::class)
sealed class LunarNode(nameKey: String,
                       abbrKey: String,
                       val northSouth: News.NorthSouth,
                       unicode: Char) :
  LunarPoint(nameKey, abbrKey, Star::class.java.name , unicode), Comparable<LunarNode> {

  /**
   * 北交點，計算方法，以下兩者結果相同
   * ApsisImpl.getPosition(Planet.MOON , Apsis.ASCENDING , gmt, Coordinate.ECLIPTIC , NodeType.TRUE);
   * StarPositionImpl.getPosition(LunarNode.NORTH_TRUE, gmt);
   */
  object NORTH : LunarNode("LunarNode.NORTH", "LunarNode.NORTH_ABBR", News.NorthSouth.NORTH, '☊') {
    override fun readResolve(): Any = NORTH
  }

  /**
   * 真實南交點，計算方法，以下兩者結果相同
   * ApsisImpl.getPosition(Planet.MOON , Apsis.DESCENDING , gmt, Coordinate.ECLIPTIC , NodeType.TRUE);
   * StarPositionImpl.getPosition(LunarNode.SOUTH_TRUE, gmt);
   */
  object SOUTH : LunarNode("LunarNode.SOUTH", "LunarNode.SOUTH_ABBR", News.NorthSouth.SOUTH,  '☋') {
    override fun readResolve(): Any = SOUTH
  }


  override fun compareTo(other: LunarNode): Int {
    if (this == other)
      return 0

    return values.indexOf(this) - values.indexOf(other)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is LunarNode) return false
    if (!super.equals(other)) return false

    if (northSouth != other.northSouth) return false

    return true
  }

  override fun hashCode(): Int {
    var result = super.hashCode()
    result = 31 * result + northSouth.hashCode()
    return result
  }

  companion object : IPoints<LunarNode> {

    override val type: KClass<out Point> = LunarNode::class

    override val values: Array<LunarNode> by lazy { arrayOf(NORTH, SOUTH) }

    override fun fromString(value: String, locale: Locale): LunarNode? {
      return values.firstOrNull {
        it.toString(locale).equals(value, ignoreCase = true)
      }
    }

    fun of(northSouth: News.NorthSouth): LunarNode {
      return values.first {
        it.northSouth == northSouth
      }
    }
  }
}
