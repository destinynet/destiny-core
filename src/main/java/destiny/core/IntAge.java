/**
 * Created by smallufo on 2017-10-15.
 */
package destiny.core;

import destiny.core.calendar.Location;
import destiny.core.calendar.TimeTools;
import org.jooq.lambda.tuple.Tuple2;

import java.time.chrono.ChronoLocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 取得一張命盤（不論是八字、斗數、或是占星）
 * 的「整數、歲數」範圍
 * <pre>
 * 例如：
 * 八字的「一歲」指的是「出生之時」到「下一個立春」為止 , 日後均以「立春」為界
 * 斗數的「一歲」指的是「出生之時」到「下一個大年初一」為止 , 日後均以「大年初一」為界
 * 日本、韓國的「一歲」指的是「出生之時」到「西元年底」為止，日後均以西元年份為界
 * 韓國另有另一種「一歲」指的是「出生之時」到「下一個三月一日」為止，日後均以「三月初一」為界
 * </pre>
 */
public interface IntAge {

  /**
   * 此時刻出生的某人，在第幾歲時，範圍為何
   * @return 前者為 prior GMT 時刻，後者為 later GMT 時刻
   * */
  Tuple2<Double , Double> getRange(Gender gender , double gmtJulDay , Location loc , int age);

  /** 承上 , 傳回 {@link ChronoLocalDateTime} 版本 */
  default Tuple2<ChronoLocalDateTime , ChronoLocalDateTime> getRangeTime(Gender gender , double gmtJulDay , Location loc , int age , Function<Double , ChronoLocalDateTime> revJulDayFunc) {
    return getRange(gender , gmtJulDay , loc , age)
      .map1(revJulDayFunc)
      .map2(revJulDayFunc);
  }

  /** 列出 fromAge 到 toAge 的結果 */
  List<Tuple2<Double , Double>> getRanges(Gender gender , double gmtJulDay , Location loc , int fromAge , int toAge);

  /** 承上 , 列出 fromAge 到 toAge 的結果 (GMT) , 傳回 Map[Age , Tuple[from , to]] */
  default Map<Integer , Tuple2<Double , Double>> getRangesMap(Gender gender , double gmtJulDay , Location loc , int fromAge , int toAge) {
    List<Tuple2<Double , Double>> list = getRanges(gender , gmtJulDay , loc , fromAge , toAge);

    return IntStream.range(0 , list.size())
      .boxed().collect(Collectors.toMap( i -> fromAge+i , list::get));
  }

  /** 承上 , 列出 fromAge 到 toAge 的結果 , 傳回 Map[Age , Tuple[from , to]] , 傳回的是 {@link ChronoLocalDateTime} , GMT 時刻 */
  default Map<Integer , Tuple2<ChronoLocalDateTime , ChronoLocalDateTime>> getRangesGmtMap(Gender gender , double gmtJulDay , Location loc , int fromAge , int toAge , Function<Double , ChronoLocalDateTime> revJulDayFunc) {
    return getRangesMap(gender , gmtJulDay , loc , fromAge , toAge)
      .entrySet().stream()
      .collect(Collectors.toMap(
        Map.Entry::getKey, entry -> {
        Tuple2<Double , Double> tuple = entry.getValue();
        return tuple.map1(revJulDayFunc).map2(revJulDayFunc);
      }));
  }

  /** 承上 , 列出 fromAge 到 toAge 的結果 , 傳回 Map[Age , Tuple[from , to]] , 傳回的是 {@link ChronoLocalDateTime} , LMT 時刻 */
  default Map<Integer , Tuple2<ChronoLocalDateTime , ChronoLocalDateTime>> getRangesLmtMap(Gender gender , double gmtJulDay , Location loc , int fromAge , int toAge , Function<Double , ChronoLocalDateTime> revJulDayFunc) {
    return getRangesMap(gender , gmtJulDay , loc , fromAge , toAge)
      .entrySet().stream()
      .collect(Collectors.toMap(
        Map.Entry::getKey, entry -> {
        Tuple2<Double , Double> tuple = entry.getValue();
        return tuple
          .map1(gmt -> TimeTools.getLmtFromGmt(gmt , loc , revJulDayFunc))
          .map2(gmt -> TimeTools.getLmtFromGmt(gmt , loc , revJulDayFunc));
      }));
  }
}
