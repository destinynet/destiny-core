/**
 * @author smallufo 
 * Created on 2008/12/11 at 上午 3:56:30
 */ 
package destiny.astrology.chart.astrolog;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

import destiny.astrology.Horoscope;
import destiny.astrology.chart.AbstractRing;
import destiny.astrology.chart.EmitLineProperties;

/** 360度環 */
public class Ring360 extends AbstractRing
{
  private Stroke strokeBold = new BasicStroke(2.0f);
  private Stroke strokeNormal = new BasicStroke(1.0f);
  
  public Ring360(Horoscope h , double innerFrom , double outerTo)
  {
    super(h , innerFrom , outerTo);
  }
  
  @Override
  public EmitLineProperties[] getEmitLineProperties()
  {
    //取得西落點是黃道幾度
    double degDesc = h.getCuspDegree(7);
    
    EmitLineProperties[] lines = new EmitLineProperties[360];
    
    for (int i = 0; i < 360 ; i++)
    {
      double 第一象限度數 = i- degDesc;

      lines[i] = new EmitLineProperties();
      lines[i].angle = 第一象限度數 ; 
      
      if (i %30 == 0)
        lines[i].style.stroke = strokeBold;
      else if (i % 5 == 0)
        lines[i].style.stroke = strokeNormal;
      else
      {
        lines[i].style.stroke = strokeNormal;
        lines[i].style.color = Color.GRAY;
      }
    }
    return lines;
  }
}
