/**
 * @author smallufo 
 * Created on 2008/12/11 at 上午 3:51:21
 */
package destiny.astrology.chart.astrolog;

import destiny.astrology.Horoscope;
import destiny.astrology.ZodiacSign;
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
import java.util.Locale;
import java.util.Map;

/** 星座環 */
public class RingSign extends AbstractRing
{
  @NotNull
  private Stroke    strokeBold = new BasicStroke(2.0f);

  public RingSign(Horoscope h , double innerFrom , double outerTo)
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

    for (int i = 0; i < 12; i++)
    {
      double 第一象限度數 = i * 30 - degDesc;

      lines[i] = new EmitLineProperties();
      lines[i].angle = 第一象限度數;
      lines[i].style.stroke = strokeBold;
    }

    return lines;
  }

  /** 星座符號 , 最好能設定顏色 */
  @Override
  public Map<Double, BufferedImage> getBfferedImages()
  {
    Map<Double, BufferedImage> result = Collections.synchronizedMap(new HashMap<Double, BufferedImage>());
    
    //取得西落點是黃道幾度
    double degDesc = h.getCuspDegree(7);
    
    ZodiacSign[] signs = ZodiacSign.values();

    for (int signIndex = 0; signIndex < 12; signIndex++)
    {
      double 第一象限度數 = signs[signIndex].getDegree() + 15 - degDesc;
      InputStream is = getClass().getResourceAsStream(("Sign." + signs[signIndex].toString(Locale.ENGLISH) + ".gif").intern());
      try
      {
        BufferedImage signImg = ImageIO.read(is);

        result.put(第一象限度數, signImg);
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

}
