/*
 * @author smallufo
 * @date 2004/10/22
 * @time 下午 10:29:42
 */
package destiny.astrology;

import destiny.core.calendar.Location;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * 2015-06-11 重寫此介面，讓此介面成為 immutable
 */
public interface IHoroscope {

  @NotNull
  Horoscope2 getHoroscope(double gmtJulDay , Location location , @NotNull Set<Point> points ,
                            HouseSystem houseSystem , Centric centric ,Coordinate coordinate ,
                          double temperature , double pressure) ;
}
