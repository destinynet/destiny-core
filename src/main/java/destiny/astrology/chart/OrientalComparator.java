/**
 * @author smallufo 
 * Created on 2008/12/16 at 上午 2:41:10
 */ 
package destiny.astrology.chart;

import java.util.Comparator;

import destiny.astrology.Horoscope;
import destiny.astrology.Point;

/** 看看兩個 Point , 誰比較在「東方」 */
public class OrientalComparator implements Comparator<Point>
{
  private Horoscope h;
  
  public OrientalComparator(Horoscope h)
  {
    this.h = h;
  }

  @Override
  public int compare(Point p1, Point p2)
  {
    /**
     * 如果兩個相等，要傳回 0 , 否則 contains() 會傳回 false , 即使兩個 point 是同個 reference !!! 
     * 好重要的一點啊！！！！！！！！！
     * 參照 http://linliangyi2007.javaeye.com/blog/165279
     * */
    if (p1.equals(p2))
      return 0;
    double deg1 = h.getPositionWithAzimuth(p1).getLongitude();
    double deg2 = h.getPositionWithAzimuth(p2).getLongitude();
    if (Horoscope.isOriental(deg1, deg2))
      return -1;
    else
      return 1;
  }

 

}
