/**
 * @author smallufo 
 * Created on 2008/12/14 at 上午 10:25:50
 */ 
package destiny.astrology.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import destiny.astrology.Horoscope;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractRing implements Ring , Serializable
{
  protected Horoscope h;

  /** 百分比 (0~1) */
  protected double innerFrom;
  
  /** 百分比 (0~1) */
  protected double outerTo;
  
  public AbstractRing(Horoscope h , double innerFrom , double outerTo)
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
  public Map<Double, BufferedImage> getBfferedImages()
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
