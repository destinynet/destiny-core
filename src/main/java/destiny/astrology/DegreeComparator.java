/**
 * @author smallufo
 * Created on 2008/1/16 at 下午 8:51:47
 */
package destiny.astrology;

import java.util.Comparator;

public class DegreeComparator implements Comparator<Point> {

  private final Horoscope horoscope;

  public DegreeComparator(Horoscope horoscope) {
    this.horoscope = horoscope;
  }

  @Override
  public int compare(Point p1, Point p2) {
    PositionWithAzimuth pos1;
    PositionWithAzimuth pos2;
    pos1 = horoscope.getPosition(p1);
    pos2 = horoscope.getPosition(p2);

    return (int) (pos1.getLng() - pos2.getLng());
  }

}
