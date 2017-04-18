/**
 * Created by smallufo on 2017-04-18.
 */
package destiny.core.chinese.ziwei;

import destiny.core.calendar.SolarTerms;
import destiny.core.chinese.Branch;

/** 取命宮地支 */
public interface IMainHouse {

  Branch getMainHouse(int month , Branch hour , SolarTerms solarTerms);
}
