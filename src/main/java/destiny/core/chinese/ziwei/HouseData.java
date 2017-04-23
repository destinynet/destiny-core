/**
 * Created by smallufo on 2017-04-15.
 */
package destiny.core.chinese.ziwei;

import destiny.core.chinese.StemBranch;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Set;

/**
 * 命盤中，一個宮位所包含的所有資訊
 */
public class HouseData implements Serializable , Comparable<HouseData> {

  //private final Map<FlowType , House> houseMap;

  /** 宮位名稱 */
  private final House house;

  /** 宮位干支 */
  private final StemBranch stemBranch;

  /** 宮位裡面 有哪些星體 */
  private final Set<ZStar> stars;

  /**
   * 大限 從何時 到何時
   * 這裡用 long , 不用 int
   * 是因為，可能以後會傳入 時間點 (幾月幾日幾點幾分幾秒...) , 則轉換成 julian day (去除小數部分)
   * */
  private final IZiwei.RangeType rangeType;
  private final double rangeFrom;
  private final double rangeTo;

  public HouseData(House house, StemBranch stemBranch, Set<ZStar> stars, IZiwei.RangeType rangeType, double rangeFrom, double rangeTo) {
    this.house = house;
    this.stemBranch = stemBranch;
    this.stars = stars;
    this.rangeType = rangeType;
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

  public IZiwei.RangeType getRangeType() {
    return rangeType;
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
