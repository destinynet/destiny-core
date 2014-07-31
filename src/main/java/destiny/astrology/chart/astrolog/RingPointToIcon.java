/**
 * @author smallufo 
 * Created on 2008/12/16 at 下午 11:59:32
 */
package destiny.astrology.chart.astrolog;

import java.awt.BasicStroke;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import destiny.astrology.Horoscope;
import destiny.astrology.Point;
import destiny.astrology.Utils;
import destiny.astrology.chart.AbstractRing;
import destiny.astrology.chart.PointConnectionUtil;
import destiny.astrology.chart.Style;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 繪製「點」到「星體icon」的連線
 */
public class RingPointToIcon extends AbstractRing
{
  private double         center;
  private double         radius;
  private RingPointIcons ringIcons;
  private Set<Point>     shownPoints;
  @NotNull
  private Stroke         strokeNormal = new BasicStroke(1.0f);

  public RingPointToIcon(Horoscope h, double center, double radius, double innerFrom, double outerTo, RingPointIcons ringIcons, Set<Point> shownPoints)
  {
    super(h, innerFrom, outerTo);
    this.center = center;
    this.radius = radius;
    this.ringIcons = ringIcons;
    this.shownPoints = shownPoints;
  }

  @Override
  public Map<Double, BufferedImage> getBufferedImages()
  {
    //取得西落點是黃道幾度
    double degDesc = h.getCuspDegree(7);

    /** 星體的位置 */
    Map<Double, BufferedImage> result = Collections.synchronizedMap(new HashMap<>());

    /** 「重新排列過的」icon 中心點的位置 */
    Map<Point, Double> iconCenterDegreeMap = ringIcons.getRearrangedIconCenterMap();

    for (Point point : shownPoints)
    {
      double innerDegree = Utils.getNormalizeDegree(h.getPositionWithAzimuth(point).getLongitude() - degDesc);
      double outerDegree = iconCenterDegreeMap.get(point);

      //System.out.println("\n星體 " + point + " : 內圈度數 : " + innerDegree + " , 外圈度數 : " + outerDegree);

      //由內畫到外

      //內圈上的點
      double innerX = center + radius * innerFrom * Math.cos(Math.toRadians(innerDegree));
      double innerY = center - radius * innerFrom * Math.sin(Math.toRadians(innerDegree));

      //外圈上的點
      double outerX = center + radius * outerTo * Math.cos(Math.toRadians(outerDegree));
      double outerY = center - radius * outerTo * Math.sin(Math.toRadians(outerDegree));

      //System.out.println("從 (" + innerX + "," + innerY + ") 畫到 (" + outerX + "," + outerY + ")");


      PointConnectionUtil pcu = new PointConnectionUtil(innerX , innerY , outerX , outerY , strokeNormal);
      
      //計算此線條的中心點
      double lineCenterX = (innerX + outerX) / 2;
      double lineCenterY = (innerY + outerY) / 2;
      
      //圓心，到「線條中點」的度數
      double centerToLineCenterAngle = RingPointIconRangeUtil.getAngle(lineCenterX, lineCenterY, center);
      result.put(centerToLineCenterAngle, pcu.getResultImage());
    }
    return result;
  }

  /** 不需要繪製內環 */
  @Nullable
  @Override
  public Style getInnerRingStyle()
  {
    return null;
  }

}
