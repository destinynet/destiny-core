/**
 * Created by smallufo on 2017-04-09.
 */
package destiny.core.chinese.ziwei;

import destiny.tools.ArrayTools;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static destiny.core.chinese.ziwei.House.*;

/**
 * 太乙
 * 相對於紫微斗數全書 的系統
 *
 * 多了 {@link House#相貌} , 但少了 {@link House#遷移}
 */
public class HouseSeqTaiyiImpl implements HouseSeqIF , Serializable {

  private final static House[] ARRAY = new House[] {
    命宮 , 兄弟 , 夫妻 ,
    子女 , 財帛 , 田宅 ,
    官祿 , 交友 , 疾厄 ,
    福德 , 相貌 , 父母
  };

  private final static List<House> list = Arrays.asList(ARRAY);

  @Override
  public String getTitle(Locale locale) {
    return "太乙人道命法";
  }

  @Override
  public String getDescription(Locale locale) {
    return "命兄夫 子財田 官奴疾 福相父";
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
    return list.indexOf(h);
  }
}
