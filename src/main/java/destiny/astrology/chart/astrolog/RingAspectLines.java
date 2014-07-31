/**
 * @author smallufo 
 * Created on 2008/12/20 at 上午 5:02:14
 */ 
package destiny.astrology.chart.astrolog;

import destiny.astrology.*;
import destiny.astrology.Point;
import destiny.astrology.chart.AbstractRing;
import destiny.astrology.chart.PointConnection;

import java.awt.*;
import java.util.*;

public class RingAspectLines extends AbstractRing
{
  private double center;
  private double radius;

  // 形成交角的資料
  private Collection<HoroscopeAspectData> aspectDataSet;
  
  // 什麼交角要用什麼顏色
  private Map<Aspect , Color> aspectColorMap = Collections.synchronizedMap(new HashMap<>());
  
  /** 最內圈 , 繪製星體交角的線條 */
  public RingAspectLines(Horoscope h, double center , double radius , double innerFrom, double outerTo , Collection<HoroscopeAspectData> aspectDataSet)
  {
    super(h, innerFrom, outerTo);
    this.center = center;
    this.radius = radius;
    this.aspectDataSet = aspectDataSet;
    
    aspectColorMap.put(Aspect.OPPOSITION, Color.BLUE); //180
    aspectColorMap.put(Aspect.SQUARE, Color.RED); //90
    aspectColorMap.put(Aspect.TRINE , Color.GREEN); //120
    aspectColorMap.put(Aspect.SEXTILE , Color.CYAN); //60
    aspectColorMap.put(Aspect.CONJUNCTION , Color.YELLOW); //0
  }

  @Override
  public Set<PointConnection> getPointConnections()
  {
    //取得西落點是黃道幾度
    double degDesc = h.getCuspDegree(7);
    
    Set<PointConnection> result = Collections.synchronizedSet(new HashSet<>());
    
    for(HoroscopeAspectData aspectData : aspectDataSet)
    {
      //取得形成交角的兩顆星體
      Iterator<Point> twoPointsIt = aspectData.getTwoPoints().iterator();
      Point p1 = twoPointsIt.next();
      Point p2 = twoPointsIt.next();
      
      
      //取得兩顆星體的度數（從第一象限算起）
      double p1deg = Utils.getNormalizeDegree(h.getPositionWithAzimuth(p1).getLongitude() - degDesc);
      double p2deg = Utils.getNormalizeDegree(h.getPositionWithAzimuth(p2).getLongitude() - degDesc);
      
      //System.out.println(p1 + " 位於 " + p1Angle + " , " + p2 + " 位於 " + p2Angle);
      
      double p1x = center + radius * outerTo * Math.cos(Math.toRadians(p1deg));
      double p1y = center - radius * outerTo * Math.sin(Math.toRadians(p1deg));
      double p2x = center + radius * outerTo * Math.cos(Math.toRadians(p2deg));
      double p2y = center - radius * outerTo * Math.sin(Math.toRadians(p2deg));
      
      //System.out.println("從 (" + p1x+","+p1y+") 畫到 (" + p2x+","+p2y+") \n");
      
      PointConnection pc = new PointConnection(p1x , p1y , p2x , p2y);
      
      Aspect aspect = aspectData.getAspect();
      double angle = Horoscope.getAngle(p1deg, p2deg);
      double orb = Math.abs(angle - aspect.getDegree());
      //System.out.println(getClass() + " \t : " + p1 + " 與 " + p2 + " 形成 " + aspect + " 角度 , 容許度 " + orb);
      pc.style.color = (aspectColorMap.get(aspect) == null) ? Color.WHITE : aspectColorMap.get(aspect); 
      
      float strokeWidth = 1.0f;
      float[] floats;
      if (orb > 10)
        floats = new float[] {1.0f , 6.0f};
      else if (orb > 8)
        floats = new float[] {1.0f , 5.0f};
      else if (orb > 6)
        floats = new float[] {1.0f , 4.0f};
      else if (orb > 4)
        floats = new float[] {1.0f , 3.0f};
      else if (orb > 2)
      {
        floats = new float[] {1.0f , 2.0f};
        strokeWidth = 1.5f;
      }
      else if (orb > 1)
      {
        floats = new float[] {1.0f , 1.0f};
        strokeWidth = 2.0f;
      }
      else
      {
        floats = new float[] {1.0f , 0.0f};
        strokeWidth = 2.0f;
      }
        
      pc.style.stroke = new BasicStroke(strokeWidth , BasicStroke.CAP_BUTT , BasicStroke.JOIN_BEVEL , 10 , floats , 0);
      
      result.add(pc);
    }
    return result;
  }

  
}
