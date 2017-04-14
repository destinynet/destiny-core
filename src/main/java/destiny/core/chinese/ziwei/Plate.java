/**
 * Created by smallufo on 2017-04-13.
 */
package destiny.core.chinese.ziwei;

import destiny.core.chinese.Branch;
import destiny.core.chinese.FiveElement;
import destiny.core.chinese.StemBranch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

/** 排盤結果 , 作為 DTO */

public class Plate implements Serializable {

  /** 命宮 */
  private final StemBranch mainHouse;

  /** 身宮 */
  private final StemBranch bodyHouse;

  /** 五行 */
  private final FiveElement fiveElement;

  /** 五行第幾局 */
  private final int set;

  /** 12宮位，分別對應的地支 */
  private final Map<House , StemBranch> houseMap;

  /** 哪些星在哪些宮位 */
  private final Map<ZStar , StemBranch> starBranchMap;

  /** 哪些地支、有哪些星體 */
  private final Map<Branch , Set<ZStar>> branchStarMap;

  /** 哪些宮位，有哪些星體 */
  private Map<House , Set<ZStar>> houseStarMap;

  private transient static Logger logger = LoggerFactory.getLogger(Plate.class);

  public Plate(StemBranch mainHouse, StemBranch bodyHouse, FiveElement fiveElement, int set, Map<House, StemBranch> houseMap, Map<ZStar, StemBranch> starBranchMap) {
    this.mainHouse = mainHouse;
    this.bodyHouse = bodyHouse;
    this.fiveElement = fiveElement;
    this.set = set;
    this.houseMap = houseMap;
    this.starBranchMap = starBranchMap;

    branchStarMap = starBranchMap.entrySet().stream()
      .collect(
        Collectors.groupingBy(
          entry -> entry.getValue().getBranch(),
          TreeMap::new,   // 保留地支順序
          Collectors.mapping(Map.Entry::getKey, Collectors.toSet())
        )
      );

    houseStarMap = houseMap.entrySet().stream()
      .filter(e -> branchStarMap.get(e.getValue().getBranch()) != null) // 某些宮位可能沒有星體，在 Java8 的 toMap 會丟出 NPE , 參照 http://stackoverflow.com/q/24630963
      .collect(Collectors.toMap(Map.Entry::getKey, e -> branchStarMap.get(e.getValue().getBranch())));
  }

  public StemBranch getMainHouse() {
    return mainHouse;
  }

  public StemBranch getBodyHouse() {
    return bodyHouse;
  }

  public FiveElement getFiveElement() {
    return fiveElement;
  }

  public int getSet() {
    return set;
  }

  /** 宮位名稱 -> 地支 */
  public Map<House, StemBranch> getHouseMap() {
    return houseMap;
  }

  /** 星體 -> 宮位地支 */
  public Map<ZStar, StemBranch> getStarBranchMap() {
    return starBranchMap;
  }

  /** 宮位地支 -> 星體s */
  public Map<Branch, Set<ZStar>> getBranchStarMap() {
    return branchStarMap;
  }

  /** 宮位名稱 -> 星體s */
  public Map<House, Set<ZStar>> getHouseStarMap() {
    return houseStarMap;
  }
}
