/**
 * Created by smallufo on 2017-04-16.
 */
package destiny.core.chinese.ziwei;

import destiny.core.chinese.Branch;

import java.io.Serializable;

/**
 * 計算流日： 固定傳回流日地支
 *
 * 說明頁面 : https://519843.blogspot.tw/2017/03/blog-post_29.html
 */
public class FlowDayFixedImpl implements IFlowDay , Serializable {

  @Override
  public Branch getFlowDay(Branch flowDayBranch, int flowDayNum, Branch flowMonthMainHouse) {
    return flowDayBranch;
  }
}
