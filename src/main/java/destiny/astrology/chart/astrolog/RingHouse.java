/**
 * @author smallufo 
 * Created on 2008/12/11 at 上午 3:59:57
 */ 
package destiny.astrology.chart.astrolog;

import destiny.astrology.Horoscope;
import destiny.astrology.chart.AbstractRing;
import destiny.astrology.chart.EmitLineProperties;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/** 地盤環 */
public class RingHouse extends AbstractRing
{
  
  @NotNull
  private Stroke strokeNormal = new BasicStroke(1.0f);
  
  public RingHouse(Horoscope h , double innerFrom , double outerTo)
  {
    super(h , innerFrom , outerTo);
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
        lines[house-1].style.color = Color.WHITE;
        lines[house-1].style.stroke = strokeNormal;
      }
    }
    return lines;
  }

  @Override
  public Map<Double, BufferedImage> getBufferedImages()
  {
    Map<Double, BufferedImage> result = Collections.synchronizedMap(new HashMap<Double, BufferedImage>());
    
    //取得西落點是黃道幾度
    double degDesc = h.getCuspDegree(7);
    
    for(int house = 1 ; house <=12 ; house++)
    {
      double center = getCenterDegree(h.getCuspDegree(house) , h.getCuspDegree(house+1) );
      double 第一象限度數 = center - degDesc;
      
      InputStream is = getClass().getResourceAsStream("Number.small." + house + ".gif");
      try
      {
        BufferedImage numberImage = ImageIO.read(is);
        result.put(第一象限度數, numberImage);
      }
      catch (IOException ignored)
      {
      }
      finally {
        try
        {
          is.close();
        }
        catch (IOException ignored)
        {
        }
      }
    }
    return result;
  }


  private double getCenterDegree(double h1 , double h2)
  {
    if (h2 < h1)
      return (h1 + h2 + 360) /2;
    else
      return (h1+h2)/2;
  }
}
