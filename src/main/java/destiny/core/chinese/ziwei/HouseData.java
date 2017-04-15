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

  /** 宮位名稱 */
  private final House house;

  /** 宮位干支 */
  private final StemBranch stemBranch;

  /** 宮位裡面 有哪些星體 */
  private final Set<ZStar> stars;

  public HouseData(House house, StemBranch stemBranch, Set<ZStar> stars) {
    this.house = house;
    this.stemBranch = stemBranch;
    this.stars = stars;
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
