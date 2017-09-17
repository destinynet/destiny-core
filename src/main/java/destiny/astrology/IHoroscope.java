/*
 * @author smallufo
 * @date 2004/10/22
 * @time 下午 10:29:42
 */
package destiny.astrology;

import destiny.core.calendar.Location;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * 2015-06-11 重寫此介面，讓此介面成為 immutable
 */
public interface IHoroscope {
  Logger logger = LoggerFactory.getLogger(IHoroscope.class);

//  @NotNull
//  Horoscope2 getHoroscope(double gmtJulDay , Location location ,
//                          @NotNull Collection<Point> points ,
//                          @NotNull HouseSystem houseSystem ,
//                          @NotNull Centric centric ,
//                          @NotNull Coordinate coordinate ,
//                          double temperature , double pressure) ;

  Horoscope2 getHoroscope(LocalDateTime lmt , Location loc , @NotNull Collection<Point> points ,
                          @NotNull HouseSystem houseSystem ,
                          @NotNull Centric centric ,
                          @NotNull Coordinate coordinate ,
                          double temperature , double pressure) ;

  default Horoscope2 getHoroscope(LocalDateTime lmt , Location loc , @NotNull Collection<Point> points ,
                          @NotNull HouseSystem houseSystem ,
                          @NotNull Centric centric ,
                          @NotNull Coordinate coordinate ) {
    return getHoroscope(lmt , loc , points , houseSystem , centric , coordinate , 0 , 1013.25);
  }
}
