/**
 * @author smallufo 
 * Created on 2008/12/20 at 下午 5:53:47
 */ 
package destiny.astrology.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.Serializable;

/** 描述兩點之間的連線 , 純粹資料結構，不做 OO 封裝 */
public class PointConnection implements Serializable
{
  /** 從這點 */
  public double x1=0,y1=0;
  
  /** 畫到這點 */
  public double x2=0,y2=0;
  
  /** 線條的 style */
  public Style style = new Style(new BasicStroke(1.0f) , Color.WHITE);

  public PointConnection(double x1 , double y1 , double x2 , double y2)
  {
    this.x1 = x1;
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;
  }
}
