/**
 * Created by smallufo on 2015-05-18.
 */
package destiny.core.calendar.eightwords.onePalm;

import destiny.core.calendar.chinese.ChineseDateIF;

import java.io.Serializable;

/**
 * 計算 {@link Palm} 的主要 context
 */
public class PalmContext implements Serializable {

  private final PalmIF palmImpl;

  /** 男順女逆 or 陽男陰女順、陰男陽女逆 */
  private final PositiveIF positiveImpl;

  private ChineseDateIF chineseDateImpl;


  public PalmContext(PalmIF palmImpl, PositiveIF positiveImpl) {
    this.palmImpl = palmImpl;
    this.positiveImpl = positiveImpl;
  }


}
