/**
 * @author smallufo 
 * Created on 2008/12/11 at 上午 5:15:15
 */ 
package destiny.astrology.chart;

import java.io.Serializable;

/**
 * 描述一條「放射線」的特徵，純粹資料結構，不做 OO 封裝
 */
public class EmitLineProperties implements Serializable
{
  public EmitLineProperties()
  {
  }
  
  /** 第一象限，的角度 */
  public double angle = 0;
  
  public Style style = new Style();
}
