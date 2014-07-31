/**
 * @author smallufo 
 * Created on 2008/12/14 at 上午 10:25:50
 */ 
package destiny.astrology.chart;

import destiny.astrology.Horoscope;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public abstract class AbstractRing implements Ring , Serializable
{
  protected Horoscope h;

  /** 百分比 (0~1) */
  protected double innerFrom;
  
  /** 百分比 (0~1) */
  protected double outerTo;
  
  protected AbstractRing(Horoscope h, double innerFrom, double outerTo)
  {
    this.h = h;
    this.innerFrom = innerFrom;
    this.outerTo = outerTo;
  }
  
  @Override
  public double getInnerFrom()
  {
    return innerFrom;
  }
  
  @Override
  public double getOuterTo()
  {
    return outerTo;
  }


  @Nullable
  @Override
  public Map<Double, BufferedImage> getBufferedImages()
  {
    return null;
  }

  @Nullable
  @Override
  public Set<PointConnection> getPointConnections()
  {
    return null;
  }

  /** 內定 , 白色實線 內環 */
  @Nullable
  @Override
  public Style getInnerRingStyle()
  {
    Style style = new Style();
    style.stroke = new BasicStroke(1.0f);
    style.color = Color.WHITE;
    return style;
  }

  @Nullable
  @Override
  public EmitLineProperties[] getEmitLineProperties()
  {
    return null;
  }

  

}
