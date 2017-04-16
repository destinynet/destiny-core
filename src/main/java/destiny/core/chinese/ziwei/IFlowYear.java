/**
 * Created by smallufo on 2017-04-16.
 */
package destiny.core.chinese.ziwei;

import destiny.core.chinese.Branch;

/** 流年排法(的命宮) : 目前只有兩個實作 : 「 流年地支」或「 流年斗君」。 */
public interface IFlowYear {

  Branch getFlowYear(Branch flowYearBranch , int birthMonth , Branch birthHour);

}
