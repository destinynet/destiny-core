/**
 * Created by smallufo on 2017-04-23.
 */
package destiny.core.chinese.ziwei;

import static destiny.core.chinese.ziwei.House.*;

public class HouseSeqAstroImpl extends HouseSeqAbstractImpl {

  private final static House[] ARRAY = new House[] {
    命宮 , 財帛 , 兄弟 ,
    田宅 , 子女 , 交友 ,
    夫妻 , 疾厄 , 遷移 ,
    官祿 , 福德 , 相貌
  };

  @Override
  public House[] getHouses() {
    return ARRAY;
  }

}
