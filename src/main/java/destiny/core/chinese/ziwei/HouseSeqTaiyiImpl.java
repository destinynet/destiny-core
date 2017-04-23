/**
 * Created by smallufo on 2017-04-09.
 */
package destiny.core.chinese.ziwei;

import static destiny.core.chinese.ziwei.House.*;

/**
 * 太乙
 * 相對於紫微斗數全書 的系統
 *
 * 多了 {@link House#相貌} , 但少了 {@link House#遷移}
 */
public class HouseSeqTaiyiImpl extends HouseSeqAbstractImpl {

  private final static House[] ARRAY = new House[] {
    命宮 , 兄弟 , 夫妻 ,
    子女 , 財帛 , 田宅 ,
    官祿 , 交友 , 疾厄 ,
    福德 , 相貌 , 父母
  };

  @Override
  public House[] getHouses() {
    return ARRAY;
  }

}
