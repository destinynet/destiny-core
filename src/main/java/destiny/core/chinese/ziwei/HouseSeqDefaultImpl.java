/**
 * Created by smallufo on 2017-04-09.
 */
package destiny.core.chinese.ziwei;

import static destiny.core.chinese.ziwei.House.*;

/** 南派 */
public class HouseSeqDefaultImpl extends HouseSeqAbstractImpl {

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

}
