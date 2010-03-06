/**
 * @author smallufo 
 * Created on 2008/12/15 at 下午 11:56:27
 */ 
package destiny.astrology.chart;

import java.awt.image.BufferedImage;

import destiny.astrology.Point;

public interface PointImageResourceReader
{
  public BufferedImage getBufferedImage(Point point);
}
