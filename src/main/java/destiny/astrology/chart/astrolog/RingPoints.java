/**
 * @author smallufo 
 * Created on 2008/12/13 at 上午 3:27:37
 */
package destiny.astrology.chart.astrolog;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import destiny.astrology.Asteroid;
import destiny.astrology.Horoscope;
import destiny.astrology.Planet;
import destiny.astrology.Point;
import destiny.astrology.chart.AbstractRing;
import destiny.astrology.chart.Style;

/** 繪製各個星體的「點」的圓環。一個星體只有一個「點」 */
public class RingPoints extends AbstractRing
{
  private Map<Point, Color>        pointColorMap   = Collections.synchronizedMap(new HashMap<Point, Color>());

  private static Map<Point, Color> defaultColorMap = Collections.synchronizedMap(new HashMap<Point, Color>());
  static
  {
    defaultColorMap.put(Planet.SUN, Color.RED);
    defaultColorMap.put(Planet.MOON, Color.BLUE);
    defaultColorMap.put(Planet.MERCURY, Color.GREEN);
    defaultColorMap.put(Planet.VENUS, Color.GREEN);
    defaultColorMap.put(Planet.MARS, Color.RED);
    defaultColorMap.put(Planet.JUPITER, Color.RED);
    defaultColorMap.put(Planet.SATURN, Color.YELLOW);
    defaultColorMap.put(Planet.URANUS, Color.GREEN);
    defaultColorMap.put(Planet.NEPTUNE, Color.BLUE);
    defaultColorMap.put(Planet.PLUTO, Color.BLUE);
    defaultColorMap.put(Asteroid.CERES, Color.PINK);
    defaultColorMap.put(Asteroid.PALLAS, Color.PINK);
    defaultColorMap.put(Asteroid.JUNO, Color.PINK);
    defaultColorMap.put(Asteroid.VESTA, Color.PINK);
    defaultColorMap.put(Asteroid.CHIRON, Color.PINK);
    defaultColorMap.put(Asteroid.PHOLUS, Color.PINK);
  }

  /** 要繪製的星體 */
  private Set<Point>               shownPoints     = Collections.synchronizedSet(new HashSet<Point>());

  public RingPoints(Horoscope h, double innerFrom, double outerTo, Set<Point> shownPoints)
  {
    super(h, innerFrom, outerTo);
    this.shownPoints = shownPoints;
    pointColorMap = defaultColorMap;
  }

  /** 設定 整組的 星體顏色 */
  public void setColorMap(Map<Point, Color> pointColorMap)
  {
    this.pointColorMap = pointColorMap;
  }

  /** 設定星體顏色 */
  public void setColor(Point point, Color color)
  {
    this.pointColorMap.put(point, color);
  }

  @Override
  public Map<Double, BufferedImage> getBfferedImages()
  {
    //取得西落點是黃道幾度
    double degDesc = h.getCuspDegree(7);

    Map<Double, BufferedImage> result = Collections.synchronizedMap(new HashMap<Double, BufferedImage>());
    for (Point point : shownPoints)
    {
      double pointDegree = h.getPositionWithAzimuth(point).getLongitude();
      double 第一象限度數 = pointDegree - degDesc;

      BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
      Graphics2D g2d = bi.createGraphics();
      Point2D.Float p2d = new Point2D.Float();
      p2d.setLocation(0.0, 0.0);
      Line2D l2d = new Line2D.Double(p2d, p2d);
      if (pointColorMap.get(point) != null)
        g2d.setColor(pointColorMap.get(point));
      else
        g2d.setColor(Color.WHITE); //沒有內定色，就畫白色
      g2d.draw(l2d);

      result.put(第一象限度數, bi);
    }
    return result;
  }

  /** 不繪製內圈 */
  @Override
  public Style getInnerRingStyle()
  {
    return null;
  }
}
