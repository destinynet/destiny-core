/**
 * Created by smallufo on 2017-04-15.
 */
package destiny.core.chinese.ziwei;

import destiny.core.chinese.Branch;
import destiny.core.chinese.StemBranch;
import kotlin.Triple;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * 命盤中，一個宮位所包含的所有資訊
 */
public class HouseData implements Serializable , Comparable<HouseData> {

  /** 宮位名稱 */
  private final House house;

  /** 宮位干支 */
  private final StemBranch stemBranch;

  /** 宮位裡面 有哪些星體 */
  private final Set<ZStar> stars;

  /** 此宮位，在各個流運，叫什麼宮位 */
  private final Map<FlowType , House> flowHouseMap;


  /**
   * 大限，從「虛歲」幾歲，到幾歲
   * */
  private final int rangeFromVage;
  private final int rangeToVage;

  /** 六條小限 */
  private final List<Integer> smallRanges;

  /** 宮干四化，此宮位，因為什麼星，各飛入哪個宮位(地支) */
  private final Set<Triple<ITransFour.Value , ZStar , Branch>> transFourFlyMap;

  private transient static Logger logger = LoggerFactory.getLogger(HouseData.class);

  public HouseData(House house, StemBranch stemBranch, Set<ZStar> stars, Map<FlowType, House> flowHouseMap,
                   Set<Triple<ITransFour.Value, ZStar, Branch>> transFourFlyMap, int rangeFromVage, int rangeToVage, List<Integer> smallRanges) {
    this.house = house;
    this.stemBranch = stemBranch;
    this.stars = stars;
    this.flowHouseMap = flowHouseMap;
    this.transFourFlyMap = transFourFlyMap;
    this.rangeFromVage = rangeFromVage;
    this.rangeToVage = rangeToVage;
    this.smallRanges = smallRanges;

    logger.debug("宮位 : {} , 宮干自化 {}" , stemBranch , transFourFlyMap);
  }

  /** 宮干自化 列表 , 長度 0 , 1 or 2 */
  public List<ITransFour.Value> getSelfTransFours() {
    // Set<Triple<ITransFour.Value , ZStar , Branch>>
    return transFourFlyMap.stream()
      .filter(t -> t.getThird() == stemBranch.getBranch())
      .map(Triple::getFirst)
      .collect(Collectors.toList());

//    return transFourFlyMap.cellSet().stream()
//      .filter(cell -> cell.getValue() == stemBranch.getBranch())
//      .map(Table.Cell::getRowKey)
//      .collect(Collectors.toList());
  }

  /** 宮干 化入對宮 */
  public List<ITransFour.Value> getOppositeTransFours() {

    return transFourFlyMap.stream()
      .filter(t -> t.getThird() == stemBranch.getBranch().getOpposite())
      .map(Triple::getFirst)
      .collect(Collectors.toList());

//    return transFourFlyMap.cellSet().stream()
//      .filter(cell -> cell.getValue() == stemBranch.getBranch().getOpposite())
//      .map(Table.Cell::getRowKey)
//      .collect(Collectors.toList());
  }

  public House getHouse() {
    return house;
  }

  public StemBranch getStemBranch() {
    return stemBranch;
  }

  public Set<ZStar> getStars() {
    return stars;
  }

  /** 傳回各個流運的宮位名稱對照 , 不傳回本命 */
  public Map<FlowType, House> getFlowHouseMapWithoutBirth() {
    return flowHouseMap.entrySet().stream()
      .filter(entry -> entry.getKey() != FlowType.本命)
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (h1, h2) -> h1, TreeMap::new) );
  }

  public int getRangeFromVage() {
    return rangeFromVage;
  }

  public int getRangeToVage() {
    return rangeToVage;
  }

  /** 六條小限 */
  public List<Integer> getSmallRanges() {
    return smallRanges;
  }

  @Override
  public String toString() {
    return "[宮位 " + "名稱=" + house + ", 干支=" + stemBranch + ", 星體=" + stars + ']';
  }

  @Override
  public int compareTo(@NotNull HouseData o) {
    if (this.equals(o))
      return 0;

    return this.house.compareTo(o.house);
  }
}
