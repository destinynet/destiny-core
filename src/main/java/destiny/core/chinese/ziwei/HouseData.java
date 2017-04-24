/**
 * Created by smallufo on 2017-04-15.
 */
package destiny.core.chinese.ziwei;

import destiny.core.chinese.FortuneOutput;
import destiny.core.chinese.StemBranch;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
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
   * 大限 從何時 到何時
   * 這裡用 long , 不用 int
   * 是因為，可能以後會傳入 時間點 (幾月幾日幾點幾分幾秒...) , 則轉換成 julian day (去除小數部分)
   * */
  private final FortuneOutput rangeOutput;
  private final double rangeFrom;
  private final double rangeTo;

  private transient static Logger logger = LoggerFactory.getLogger(HouseData.class);

  public HouseData(House house, StemBranch stemBranch, Set<ZStar> stars, Map<FlowType, House> flowHouseMap, FortuneOutput rangeOutput, double rangeFrom, double rangeTo) {
    this.house = house;
    this.stemBranch = stemBranch;
    this.stars = stars;
    this.flowHouseMap = flowHouseMap;
    this.rangeOutput = rangeOutput;
    this.rangeFrom = rangeFrom;
    this.rangeTo = rangeTo;
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

  public FortuneOutput getRangeOutput() {
    return rangeOutput;
  }

  public double getRangeFrom() {
    return rangeFrom;
  }

  public double getRangeTo() {
    return rangeTo;
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
