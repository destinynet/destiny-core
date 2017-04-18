/**
 * Created by smallufo on 2017-04-18.
 */
package destiny.core.chinese.ziwei;

import destiny.core.chinese.Branch;

/** 流時命宮 */
public interface IFlowHour {

  /**
   * @param hour  欲求算的當日時辰
   * @param flowDayMainHour 當日命宮
   */
  Branch getFlowHour(Branch hour , Branch flowDayMainHour);
}
