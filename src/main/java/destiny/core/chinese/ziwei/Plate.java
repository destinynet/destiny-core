/**
 * Created by smallufo on 2017-04-13.
 */
package destiny.core.chinese.ziwei;

import destiny.core.chinese.FiveElement;
import destiny.core.chinese.StemBranch;

import java.io.Serializable;
import java.util.Map;

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

  public Plate(StemBranch mainHouse, StemBranch bodyHouse, FiveElement fiveElement, int set, Map<House, StemBranch> houseMap, Map<ZStar, StemBranch> starBranchMap) {
    this.mainHouse = mainHouse;
    this.bodyHouse = bodyHouse;
    this.fiveElement = fiveElement;
    this.set = set;
    this.houseMap = houseMap;
    this.starBranchMap = starBranchMap;
  }

  public StemBranch getMainHouse() {
    return mainHouse;
  }
}
