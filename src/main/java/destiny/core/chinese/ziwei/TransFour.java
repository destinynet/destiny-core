/**
 * Created by smallufo on 2017-04-12.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Descriptive;
import destiny.core.chinese.Stem;

/** 四化 */
public interface TransFour extends Descriptive {

  enum Type {祿 , 權 , 科 , 忌};

  ZStar getStarOf(Stem year , Type type);
}
