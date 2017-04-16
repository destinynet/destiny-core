/**
 * Created by smallufo on 2017-04-16.
 */
package destiny.core.chinese.ziwei;

import destiny.core.chinese.Branch;

import java.io.Serializable;

/**
 * 紫微斗數全集 , 流年斗君
 *
 * 大多數北派 占驗派 還有部分南派 都用此法
 *
 * 古籍 : http://imgur.com/LzdunCh
 *
 * 參考資料 http://www.freehoro.net/ZWDS/Tutorial/PaiPan/19-0_Zi_Liu_NianDouJun.php
 */
public class FlowMonthDefaultImpl implements IFlowMonth , Serializable {

  @Override
  public Branch getFlowMonth(Branch flowYear, Branch flowMonth, int birthMonth, Branch birthHour) {
    return IZiwei
      .getFlowYearAnchor(flowYear , birthMonth , birthHour)
      .next(flowMonth.getAheadOf(Branch.寅));// 順數至流月
  }
}
