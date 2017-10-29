/**
 * Created by smallufo at 2008/11/11 下午 5:21:34
 */
package destiny.astrology;

import destiny.core.calendar.TimeTools;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.time.chrono.ChronoLocalDateTime;
import java.util.function.Function;

/**
 * 計算星體在黃道帶上 逆行 / Stationary (停滯) 的介面，目前 SwissEph 的實作只支援 Planet , Asteroid , Moon's Node (只有 True Node。 Mean 不會逆行！)
 * SwissEph 內定實作是 RetrogradeImpl
 */
public interface RetrogradeIF {

  enum StationaryType {
    DIRECT_TO_RETROGRADE,  // 順轉逆
    RETROGRADE_TO_DIRECT    // 逆轉順
  }

  /** 下次停滯的時間為何時 (GMT) */
  double getNextStationary(Star star, double fromGmt, boolean isForward);

  /**
   * 承上，不僅計算下次（或上次）的停滯時間
   * 另外計算，該次停滯，是準備「順轉逆」，或是「逆轉順」
   */
  default Tuple2<Double, StationaryType> getNextStationary(Star star, double fromGmt, boolean isForward, StarPositionIF starPositionImpl) {
    double nextStationary = getNextStationary(star, fromGmt, isForward);
    // 分別取 滯留前、後 來比對
    double prior = nextStationary - (1/1440.0);
    double after = nextStationary + (1/1440.0);
    Position pos1 = starPositionImpl.getPosition(star, prior, Centric.GEO, Coordinate.ECLIPTIC);
    Position pos2 = starPositionImpl.getPosition(star, after, Centric.GEO, Coordinate.ECLIPTIC);
    if (pos1.getSpeedLng() > 0 && pos2.getSpeedLng() < 0)
      return Tuple.tuple(nextStationary, StationaryType.DIRECT_TO_RETROGRADE);
    else if (pos1.getSpeedLng() < 0 && pos2.getSpeedLng() > 0)
      return Tuple.tuple(nextStationary, StationaryType.RETROGRADE_TO_DIRECT);
    throw new RuntimeException("Error , 前一秒 speed = " + pos1.getSpeedLng() + " , 後一秒 speed = " + pos2.getSpeedLng());
  }

  /** 下次停滯的時間為何時 (GMT) */
  default ChronoLocalDateTime getNextStationaryGMT(Star star, ChronoLocalDateTime fromGmt, boolean isForward, Function<Double, ChronoLocalDateTime> revJulDayFunc) {
    double fromGmtJulDay = TimeTools.getGmtJulDay(fromGmt);
    double resultGmt = getNextStationary(star, fromGmtJulDay, isForward);
    return revJulDayFunc.apply(resultGmt);
  }
}
