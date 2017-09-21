/**
 * @author smallufo 
 * Created on 2007/12/23 at 上午 5:25:44
 */ 
package destiny.astrology;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static destiny.astrology.LunarNode.NorthSouth.NORTH;
import static destiny.astrology.LunarNode.NorthSouth.SOUTH;
import static destiny.astrology.NodeType.MEAN;
import static destiny.astrology.NodeType.TRUE;


public final class LunarNode extends LunarPoint implements Comparable<LunarNode> {

  public enum NorthSouth {NORTH , SOUTH}

  @NotNull
  private final NorthSouth northSouth;

  @NotNull
  private final NodeType nodeType;
  
  private final static String resource = Star.class.getName();
  
  /**
   * 真實北交點，計算方法，以下兩者結果相同
   * <pre>
   * ApsisImpl.getPosition(Planet.MOON , Apsis.ASCENDING , gmt, Coordinate.ECLIPTIC , NodeType.TRUE);
   * StarPositionImpl.getPosition(LunarNode.NORTH_TRUE, gmt);
   * </pre>
   */
  @NotNull
  public static final LunarNode NORTH_TRUE = new LunarNode("LunarNode.NORTH" , "LunarNode.NORTH_ABBR" , NORTH , TRUE);

  /**
   * 平均北交點，計算方法，以下兩者結果相同
   * <pre>
   * ApsisImpl.getPosition(Planet.MOON , Apsis.ASCENDING , gmt, Coordinate.ECLIPTIC , NodeType.MEAN);
   * StarPositionImpl.getPosition(LunarNode.NORTH_MEAN, gmt);
   * </pre>
   */
  @NotNull
  public static final LunarNode NORTH_MEAN = new LunarNode("LunarNode.NORTH" , "LunarNode.NORTH_ABBR" , NORTH , MEAN);
  
  /**
   * 真實南交點，計算方法，以下兩者結果相同
   * <pre>
   * ApsisImpl.getPosition(Planet.MOON , Apsis.DESCENDING , gmt, Coordinate.ECLIPTIC , NodeType.TRUE);
   * StarPositionImpl.getPosition(LunarNode.SOUTH_TRUE, gmt);
   * </pre>
   */
  @NotNull
  public static final LunarNode SOUTH_TRUE = new LunarNode("LunarNode.SOUTH" , "LunarNode.SOUTH_ABBR" , SOUTH , TRUE);
  
  /**
   * 平均南交點，計算方法，以下兩者結果相同
   * <pre>
   * ApsisImpl.getPosition(Planet.MOON , Apsis.DESCENDING , gmt, Coordinate.ECLIPTIC , NodeType.MEAN);
   * StarPositionImpl.getPosition(LunarNode.SOUTH_MEAN, gmt);
   * </pre>
   */
  @NotNull
  public static final LunarNode SOUTH_MEAN = new LunarNode("LunarNode.SOUTH" , "LunarNode.SOUTH_ABBR" , SOUTH , MEAN);

  private static final LunarNode[] inner_values = {NORTH_TRUE , NORTH_MEAN , SOUTH_TRUE , SOUTH_MEAN};
  public static final LunarNode[] true_values = { NORTH_TRUE , SOUTH_TRUE};
  public static final LunarNode[] mean_values = { NORTH_MEAN , SOUTH_MEAN};
  public static final LunarNode[] values = { NORTH_MEAN , SOUTH_MEAN};

  LunarNode(String nameKey, String abbrKey, @NotNull NorthSouth northSouth, @NotNull NodeType nodeType)
  {
    super(nameKey, abbrKey, resource);
    this.northSouth = northSouth;
    this.nodeType = nodeType;
  }

  @NotNull
  public static LunarNode of(NorthSouth northSouth , NodeType nodeType) {
    return Arrays.stream(inner_values)
      .filter(lunarNode -> lunarNode.northSouth == northSouth && lunarNode.nodeType == nodeType)
      .findFirst().orElseThrow(() -> new RuntimeException("Cannot find LunarNode from " + northSouth + " and " + nodeType));
  }

  @NotNull
  public NodeType getNodeType()
  {
    return nodeType;
  }

  @NotNull
  public NorthSouth getNorthSouth()
  {
    return northSouth;
  }


  @Override
  public int compareTo(@NotNull LunarNode o)
  {
    if (this.equals(o))
      return 0;
    
    List<LunarNode> list = Arrays.asList(values);
    return list.indexOf(this) - list.indexOf(o);
  }


  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof LunarNode))
      return false;
    if (!super.equals(o))
      return false;
    LunarNode lunarNode = (LunarNode) o;
    return northSouth == lunarNode.northSouth && nodeType == lunarNode.nodeType;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), northSouth, nodeType);
  }
}
