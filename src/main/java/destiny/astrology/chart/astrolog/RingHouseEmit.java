/**
 * @author smallufo 
 * Created on 2008/12/24 at 上午 1:12:45
 */ 
package destiny.astrology.chart.astrolog;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

import destiny.astrology.Horoscope;
import destiny.astrology.chart.AbstractRing;
import destiny.astrology.chart.EmitLineProperties;
import org.jetbrains.annotations.NotNull;

/** 從圓心，放射到十二宮，的線條 */
public class RingHouseEmit extends AbstractRing
{
  @NotNull
  private Stroke strokeNormal = new BasicStroke(1.0f);
  
  public RingHouseEmit(Horoscope h, double innerFrom, double outerTo)
  {
    super(h, innerFrom, outerTo);
  }

  @NotNull
  @Override
  public EmitLineProperties[] getEmitLineProperties()
  {
  //取得西落點是黃道幾度
    double degDesc = h.getCuspDegree(7);
    
    EmitLineProperties[] lines = new EmitLineProperties[12];
    for(int house = 1 ; house <=12 ; house++)
    {
      double degHouse = h.getCuspDegree(house);
      double 第一象限度數 = degHouse- degDesc;
      
      lines[house-1] = new EmitLineProperties();
      lines[house-1].angle = 第一象限度數;
      if (house % 3 == 1)
        lines[house-1].style.stroke = strokeNormal;
      else
      {
        lines[house-1].style.color = Color.GRAY;
        lines[house-1].style.stroke = strokeNormal;
      }
    }
    return lines;
  }

  
}
