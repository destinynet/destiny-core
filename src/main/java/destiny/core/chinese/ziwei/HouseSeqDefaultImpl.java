/**
 * Created by smallufo on 2017-04-09.
 */
package destiny.core.chinese.ziwei;

import destiny.tools.ArrayTools;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Locale;

import static destiny.core.chinese.ziwei.House.*;

public class HouseSeqDefaultImpl implements HouseSeqIF , Serializable {

  private final static House[] ARRAY = new House[] {
    命宮 , 兄弟 , 夫妻 ,
    子女 , 財帛 , 疾厄 ,
    遷移 , 交友 , 官祿 ,
    田宅 , 福德 , 父母
  };

  @Override
  public House[] getHouses() {
    return ARRAY;
  }

  @Override
  public String getTitle(Locale locale) {
    return "紫微斗數全書";
  }

  @Override
  public String getDescription(Locale locale) {
    return "命兄夫 子財疾 遷奴官 田福父";
  }

  @Override
  public House next(House from, int n) {
    return get(getIndex(from) + n);
  }

  @Override
  public int getAheadOf(House h1, House h2) {
    int index1 = getIndex(h1);
    int index2 = getIndex(h2);
    if (index1 < 0 || index2 < 0)
      return -1;
    int steps = index1 - index2;
    return (steps >=0 ? steps : steps + 12);
  }

  private static House get(int index) {
    return ArrayTools.get(ARRAY , index);
  }

  private static int getIndex(House h) {
    return Arrays.binarySearch(ARRAY , h);
  }
}
