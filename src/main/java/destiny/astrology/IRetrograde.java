/**
 * Created by smallufo at 2008/11/11 下午 5:21:34
 */
package destiny.astrology;

import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 計算星體在黃道帶上 逆行 / Stationary (停滯) 的介面，目前 SwissEph 的實作只支援 Planet , Asteroid , Moon's Node (只有 True Node。 Mean 不會逆行！)
 * SwissEph 內定實作是 RetrogradeImpl
 */
public interface IRetrograde {

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
  default Tuple2<Double, StationaryType> getNextStationary(Star star, double fromGmt, boolean isForward, IStarPosition starPositionImpl) {
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
    throw new RuntimeException("Error , 滯留前 speed = " + pos1.getSpeedLng() + " , 滯留後 speed = " + pos2.getSpeedLng());
  }


  /**
   * 列出一段時間內，某星體的順逆過程
   */
  default List<Tuple2<Double , StationaryType>> getPeriodStationary(Star star , double fromGmt , double toGmt , IStarPosition starPositionImpl) {
    if (fromGmt >= toGmt) {
      throw new RuntimeException("toGmt ("+toGmt+") >= fromGmt("+fromGmt+")");
    }

    double gmt = fromGmt;
    List<Tuple2<Double , StationaryType>> list = new ArrayList<>();
    while (gmt < toGmt) {
      Tuple2<Double, StationaryType> result = getNextStationary(star , gmt , true , starPositionImpl);
      if (result.v1() <= toGmt) {
        list.add(result);
      }
      gmt = result.v1() + (1/1440.0);
    }
    return list;
  }

  /**
   * 取得一段時間內，這些星體的順逆過程，按照時間排序
   */
  default List<Tuple3<Double , Star , StationaryType>> getPeriodStationary(Collection<Star> stars , double fromGmt , double toGmt , IStarPosition starPositionImpl) {
    Map<Double , Tuple2<Star , StationaryType>> map = stars
      .stream()
      .flatMap(star -> {
        List<Tuple2<Double , StationaryType>> list = getPeriodStationary(star, fromGmt, toGmt, starPositionImpl);
        return list.stream().map(t -> Tuple.tuple(t.v1() , star , t.v2()));
      }).collect(Collectors.toMap(Tuple3::v1, t -> Tuple.tuple(t.v2() , t.v3()) , (v1, v2) -> v1 , TreeMap::new));

    return map.entrySet().stream()
      .map(entry -> Tuple.tuple(entry.getKey() , entry.getValue().v1() , entry.getValue().v2()))
      .collect(Collectors.toList());
  }

}
