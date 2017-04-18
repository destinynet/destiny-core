/**
 * Created by smallufo on 2017-04-19.
 */
package destiny.core.chinese.ziwei;

import destiny.core.chinese.Branch;

public interface IStrength {

  /**
   * 此顆星，在此地支宮位，強度為何
   * 1 為最強， 7 為最弱
   * 參照表格 https://goo.gl/y6Qlt3
   * */
  int getStrength(ZStar star , Branch branch);
}
