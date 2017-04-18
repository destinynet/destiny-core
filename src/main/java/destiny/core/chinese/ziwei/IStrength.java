/**
 * Created by smallufo on 2017-04-19.
 */
package destiny.core.chinese.ziwei;

import destiny.core.chinese.Branch;

public interface IStrength {

  /**
   * 此顆星，於此地支中，強度為何
   * 1 最強 , 7 最弱
   * */
  int getStrength(ZStar star , Branch branch);
}
