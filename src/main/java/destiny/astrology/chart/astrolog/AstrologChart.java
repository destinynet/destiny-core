/**
 * @author smallufo 
 * Created on 2008/12/11 at 上午 4:09:30
 */
package destiny.astrology.chart.astrolog;

import destiny.astrology.*;
import destiny.astrology.Point;
import destiny.astrology.chart.EmitLineProperties;
import destiny.astrology.chart.PointConnection;
import destiny.astrology.chart.Ring;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class AstrologChart extends BufferedImage
{
  Color              bg           = Color.BLACK;
  Color              fore         = Color.WHITE;
  double             center;
  private Stroke     strokeNormal = new BasicStroke(1.0f);

  private List<Ring> rings        = Collections.synchronizedList(new ArrayList<Ring>());

  /** 要繪製的星體 */
  private Set<Point> shownPoints  = Collections.synchronizedSet(new HashSet<Point>());
  
  private HoroscopeAspectsCalculator calculator;

  public AstrologChart(Horoscope horoscope, int width)
  {
    super(width, width, BufferedImage.TYPE_INT_RGB);

    center = width / 2;
    Graphics2D g = this.createGraphics();
    // g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    // 填滿背景
    g.setColor(bg);
    g.fillRect(0, 0, width, width);

    // 設定前景色
    g.setColor(fore);

    // 畫出水平線
    g.drawLine(0, width / 2, width, width / 2);

    // 最外圈 , 圓的半徑
    double radius = (width / 2) * 0.95;

    Ellipse2D.Double 最外圓環 = new Ellipse2D.Double(center - radius, center - radius, radius * 2, radius * 2);
    g.draw(最外圓環);

    
    //繪製行星
    for (Planet planet : Planet.values)
      shownPoints.add(planet);
    
    //繪製北交點
    shownPoints.add(LunarNode.NORTH_MEAN);

    
    calculator = new HoroscopeAspectsCalculator(horoscope , new HoroscopeAspectsCalculatorModern());
    Set<Aspect> aspects = new HashSet<Aspect>();
    aspects.addAll(Aspect.getAngles(Aspect.Importance.HIGH));
    Collection<HoroscopeAspectData> aspectDatas = calculator.getAspectDataSet( shownPoints , aspects);
    
    //開始由外往內，一環一環畫出來

    // 星座
    Ring ringSign = new RingSign(horoscope, 0.85, 1.0);
    // 360度
    Ring ring360 = new Ring360(horoscope, 0.8, 0.85);
    // 地盤
    Ring ringHouse = new RingHouse(horoscope, 0.7, 0.8);
    // 從圓心，放射到十二宮，的線條
    Ring ringHouseEmit = new RingHouseEmit(horoscope , 0 , 0.7);
    // 行星 Icons
    Ring ringPlanetIcons = new RingPointIcons(horoscope, 0.60, 0.7, radius, center , shownPoints);
    // 繪製星體的「點」，到星體的 icon , 的連線
    Ring ringPointToIcon = new RingPointToIcon(horoscope , center , radius , 0.55 , 0.60 , (RingPointIcons)ringPlanetIcons , shownPoints);
    // 星體「點」
    Ring ringPoints = new RingPoints(horoscope, 0.54, 0.55 , shownPoints);
    // 繪製星體交角的連線
    Ring ringAspectLines = new RingAspectLines(horoscope, center , radius , 0.0 , 0.52 , aspectDatas);

    rings.add(ringSign);
    rings.add(ring360);
    rings.add(ringHouse);
    rings.add(ringHouseEmit);
    rings.add(ringPlanetIcons);
    rings.add(ringPointToIcon);
    rings.add(ringPoints);
    rings.add(ringAspectLines);

    for (Ring ring : rings)
    {
      double inner = ring.getInnerFrom();
      double outer = ring.getOuterTo();

      if (ring.getEmitLineProperties() != null)
      {
        //繪製放射線條
        for (EmitLineProperties line : ring.getEmitLineProperties())
        {
          double deg = line.angle;

          double x1 = center + radius * inner * Math.cos(Math.toRadians(deg));
          double y1 = center - radius * inner * Math.sin(Math.toRadians(deg));
          double x2 = center + radius * outer * Math.cos(Math.toRadians(deg));
          double y2 = center - radius * outer * Math.sin(Math.toRadians(deg));

          g.setColor(line.style.color == null ? fore : line.style.color);
          g.setStroke(line.style.stroke == null ? strokeNormal : line.style.stroke);

          Line2D.Double line2d = new Line2D.Double();
          line2d.setLine(x1, y1, x2, y2);
          g.draw(line2d);
        }
      }

      g.setColor(fore);
      g.setStroke(strokeNormal);

      //繪製符號
      Map<Double, BufferedImage> images = ring.getBfferedImages();
      if (images != null)
      {
        for (Double deg : images.keySet())
        {
          double x = center + radius * (inner + outer) / 2 * Math.cos(Math.toRadians(deg));
          double y = center - radius * (inner + outer) / 2 * Math.sin(Math.toRadians(deg));

          BufferedImage image = images.get(deg);
          int imageW = image.getWidth();
          int imageH = image.getHeight();

          double newX = x - imageW / 2;
          double newY = y - imageH / 2;

          // 設定黑色為透明色
          /*
          g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
          g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER , 1.0f));
          //g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP , 0.0f));
          g.setPaint(Color.BLACK); //對黑色透明
          Rectangle2D.Double rect = new Rectangle2D.Double(newX,newY,imageW,imageH); 
          g.fill(rect);
          */

          
          g.drawImage(image, null, (int) newX, (int) newY);
        }
      }
      
      //繪製線條連線（交角線條）
      Set<PointConnection> pointConnections = ring.getPointConnections();
      if (pointConnections != null)
      {
        for(PointConnection conn : pointConnections)
        {
          g.setColor(conn.style.color);
          g.setStroke(conn.style.stroke);
          
          Line2D.Double line2d = new Line2D.Double();
          line2d.setLine(conn.x1, conn.y1, conn.x2, conn.y2);
          g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
          g.draw(line2d);
          g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        }
      }

      if (ring.getInnerRingStyle() != null)
      {
        g.setStroke(ring.getInnerRingStyle().stroke);
        g.setColor(ring.getInnerRingStyle().color);
        //繪製內圈環
        Ellipse2D.Double innerRing = new Ellipse2D.Double(center - radius * inner, center - radius * inner, radius * inner * 2, radius * inner * 2);
        g.draw(innerRing);
      }
    }

    g.dispose();
  }
}
