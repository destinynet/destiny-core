/**
 * @author smallufo 
 * Created on 2008/12/13 at 上午 3:27:37
 */
package destiny.astrology.chart.astrolog;

import com.google.common.collect.ImmutableMap;
import destiny.astrology.Asteroid;
import destiny.astrology.Horoscope;
import destiny.astrology.Planet;
import destiny.astrology.Point;
import destiny.astrology.chart.AbstractRing;
import destiny.astrology.chart.Style;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.*;

/** 繪製各個星體的「點」的圓環。一個星體只有一個「點」 */
public class RingPoints extends AbstractRing
{
  private Map<Point, Color>        pointColorMap   = Collections.synchronizedMap(new HashMap<Point, Color>());

  private final static ImmutableMap<Point , Color> defaultColorMap = new ImmutableMap.Builder<Point , Color>()
    .put(Planet.SUN, Color.RED)
    .put(Planet.MOON, Color.BLUE)
    .put(Planet.MERCURY, Color.GREEN)
    .put(Planet.VENUS, Color.GREEN)
    .put(Planet.MARS, Color.RED)
    .put(Planet.JUPITER, Color.RED)
    .put(Planet.SATURN, Color.YELLOW)
    .put(Planet.URANUS, Color.GREEN)
    .put(Planet.NEPTUNE, Color.BLUE)
    .put(Planet.PLUTO, Color.BLUE)
    .put(Asteroid.CERES, Color.PINK)
    .put(Asteroid.PALLAS, Color.PINK)
    .put(Asteroid.JUNO, Color.PINK)
    .put(Asteroid.VESTA, Color.PINK)
    .put(Asteroid.CHIRON, Color.PINK)
    .put(Asteroid.PHOLUS, Color.PINK)
    .build();

  /** 要繪製的星體 */
  private Set<Point>               shownPoints     = Collections.synchronizedSet(new HashSet<Point>());

  public RingPoints(Horoscope h, double innerFrom, double outerTo, Set<Point> shownPoints)
  {
    super(h, innerFrom, outerTo);
    this.shownPoints = shownPoints;
    this.pointColorMap = new HashMap<Point , Color>(defaultColorMap);
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
  public Map<Double, BufferedImage> getBufferedImages()
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

      g2d.dispose();
      result.put(第一象限度數, bi);
    }
    return result;
  }

  /** 不繪製內圈 */
  @Nullable
  @Override
  public Style getInnerRingStyle()
  {
    return null;
  }
}
