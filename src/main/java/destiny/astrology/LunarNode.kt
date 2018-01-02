/**
 * @author smallufo
 * Created on 2007/12/23 at 上午 5:25:44
 */
package destiny.astrology

import destiny.astrology.NodeType.MEAN
import destiny.astrology.NodeType.TRUE

object LunarNodes {
  val inner_values = arrayOf(LunarNode.NORTH_TRUE, LunarNode.NORTH_MEAN, LunarNode.SOUTH_TRUE, LunarNode.SOUTH_MEAN)
  val trueArray = arrayOf(LunarNode.NORTH_TRUE, LunarNode.SOUTH_TRUE)
  val meanArray = arrayOf(LunarNode.NORTH_MEAN, LunarNode.SOUTH_MEAN)
}

sealed class LunarNode (nameKey: String, abbrKey: String, val northSouth: NorthSouth, val nodeType: NodeType) :
  LunarPoint(nameKey, abbrKey, Star::class.java.name), Comparable<LunarNode> {

  /**
     * 真實北交點，計算方法，以下兩者結果相同
     * <pre>
     * ApsisImpl.getPosition(Planet.MOON , Apsis.ASCENDING , gmt, Coordinate.ECLIPTIC , NodeType.TRUE);
     * StarPositionImpl.getPosition(LunarNode.NORTH_TRUE, gmt);
    </pre> *
     */
    object NORTH_TRUE : LunarNode("LunarNode.NORTH", "LunarNode.NORTH_ABBR", NorthSouth.NORTH, TRUE)

    /**
     * 平均北交點，計算方法，以下兩者結果相同
     * <pre>
     * ApsisImpl.getPosition(Planet.MOON , Apsis.ASCENDING , gmt, Coordinate.ECLIPTIC , NodeType.MEAN);
     * StarPositionImpl.getPosition(LunarNode.NORTH_MEAN, gmt);
    </pre> *
     */
    object NORTH_MEAN : LunarNode("LunarNode.NORTH", "LunarNode.NORTH_ABBR", NorthSouth.NORTH, MEAN)

    /**
     * 真實南交點，計算方法，以下兩者結果相同
     * <pre>
     * ApsisImpl.getPosition(Planet.MOON , Apsis.DESCENDING , gmt, Coordinate.ECLIPTIC , NodeType.TRUE);
     * StarPositionImpl.getPosition(LunarNode.SOUTH_TRUE, gmt);
    </pre> *
     */
    object SOUTH_TRUE : LunarNode("LunarNode.SOUTH", "LunarNode.SOUTH_ABBR", NorthSouth.SOUTH, TRUE)

    /**
     * 平均南交點，計算方法，以下兩者結果相同
     * <pre>
     * ApsisImpl.getPosition(Planet.MOON , Apsis.DESCENDING , gmt, Coordinate.ECLIPTIC , NodeType.MEAN);
     * StarPositionImpl.getPosition(LunarNode.SOUTH_MEAN, gmt);
    </pre> *
     */
    object SOUTH_MEAN : LunarNode("LunarNode.SOUTH", "LunarNode.SOUTH_ABBR", NorthSouth.SOUTH, MEAN)





  override fun compareTo(other: LunarNode): Int {
    if (this == other)
      return 0

    return LunarNodes.inner_values.indexOf(this) - LunarNodes.inner_values.indexOf(other)
  }


  companion object {

    fun of(northSouth: NorthSouth, nodeType: NodeType): LunarNode {
      return LunarNodes.inner_values.first {
        it.northSouth == northSouth && it.nodeType === nodeType
      }
    }
  }
}
