/**
 * @author smallufo 
 * Created on 2008/12/13 at 上午 3:17:53
 */ 
package destiny.astrology.chart;

import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.awt.Stroke;
import java.io.Serializable;

/** 描述顏色 以及 Stroke 的 純粹資料結構，不做 OO 封裝 */
public class Style implements Serializable
{
  /** stroke */
  @Nullable
  public Stroke stroke = null;
  
  /** 顏色 */
  @Nullable
  public Color color = null;
  
  public Style()
  {
  }

  
  public Style(Stroke stroke , Color color)
  {
    this.stroke = stroke;
    this.color = color;
  }
}
