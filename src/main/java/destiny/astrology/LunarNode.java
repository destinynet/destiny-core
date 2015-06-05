/**
 * @author smallufo 
 * Created on 2007/12/23 at 上午 5:25:44
 */ 
package destiny.astrology;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;



public final class LunarNode extends LunarPoint implements Comparable<LunarNode>
{
  public enum NorthSouth {NORTH , SOUTH}

  private NorthSouth northSouth = NorthSouth.NORTH;
  
  private NodeType nodeType= NodeType.MEAN ;
  
  private final static String resource = "destiny.astrology.Star";
  
  /**
   * 真實北交點，計算方法，以下兩者結果相同
   * <pre>
   * ApsisImpl.getPosition(Planet.MOON , Apsis.ASCENDING , gmt, Coordinate.ECLIPTIC , NodeType.TRUE);
   * StarPositionImpl.getPosition(LunarNode.NORTH_TRUE, gmt);
   * </pre>
   */
  @NotNull
  public static LunarNode NORTH_TRUE = new LunarNode("LunarNode.NORTH" , "LunarNode.NORTH_ABBR" , NorthSouth.NORTH , NodeType.TRUE);

  /**
   * 平均北交點，計算方法，以下兩者結果相同
   * <pre>
   * ApsisImpl.getPosition(Planet.MOON , Apsis.ASCENDING , gmt, Coordinate.ECLIPTIC , NodeType.MEAN);
   * StarPositionImpl.getPosition(LunarNode.NORTH_MEAN, gmt);
   * </pre>
   */
  @NotNull
  public static LunarNode NORTH_MEAN = new LunarNode("LunarNode.NORTH" , "LunarNode.NORTH_ABBR" , NorthSouth.NORTH , NodeType.MEAN);
  
  /**
   * 真實南交點，計算方法，以下兩者結果相同
   * <pre>
   * ApsisImpl.getPosition(Planet.MOON , Apsis.DESCENDING , gmt, Coordinate.ECLIPTIC , NodeType.TRUE);
   * StarPositionImpl.getPosition(LunarNode.SOUTH_TRUE, gmt);
   * </pre>
   */
  @NotNull
  public static LunarNode SOUTH_TRUE = new LunarNode("LunarNode.SOUTH" , "LunarNode.SOUTH_ABBR" , NorthSouth.SOUTH , NodeType.TRUE);
  
  /**
   * 平均南交點，計算方法，以下兩者結果相同
   * <pre>
   * ApsisImpl.getPosition(Planet.MOON , Apsis.DESCENDING , gmt, Coordinate.ECLIPTIC , NodeType.MEAN);
   * StarPositionImpl.getPosition(LunarNode.SOUTH_MEAN, gmt);
   * </pre>
   */
  @NotNull
  public static LunarNode SOUTH_MEAN = new LunarNode("LunarNode.SOUTH" , "LunarNode.SOUTH_ABBR" , NorthSouth.SOUTH , NodeType.MEAN);

  //public static LunarNode[] values = {NORTH_TRUE , NORTH_MEAN , SOUTH_TRUE , SOUTH_MEAN};
  @NotNull
  public static LunarNode[] values = { NORTH_MEAN , SOUTH_MEAN};
  
  protected LunarNode(String nameKey, String abbrKey , NorthSouth northSouth , NodeType nodeType)
  {
    super(nameKey, abbrKey, resource);
    this.northSouth = northSouth;
    this.nodeType = nodeType;
  }

  public NodeType getNodeType()
  {
    return nodeType;
  }

  public NorthSouth getNorthSouth()
  {
    return northSouth;
  }


  @Override
  public int compareTo(LunarNode o)
  {
    if (this.equals(o))
      return 0;
    
    List<LunarNode> list = Arrays.asList(values);
    return list.indexOf(this) - list.indexOf(o);
  }

}
