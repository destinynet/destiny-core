/**
 * @author smallufo
 * @date 2002/9/23
 * @time 下午 03:02:51
 */
package destiny.FengShui.SanYuan;

import java.io.Serializable;

abstract class AbstractCompass implements Serializable
{
  /**
   * 取得某個此輪初始元素的起始度數
   */  
  public abstract double getInitDegree();
  
  /**
   * 取得此輪每個元素的間隔度數
   */
  public abstract double getStepDegree();
  
  /**
   * 取得某個元素的起始度數
   */
  public abstract double getStartDegree(Object o);
  
  /**
   * 取得某個元素的結束度數
   */
  public abstract double getEndDegree(Object o);
  
}
