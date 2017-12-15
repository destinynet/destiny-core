/*
 * @author smallufo
 * @date 2004/10/29
 * @time 下午 09:57:05
 */
package destiny.astrology;

import destiny.core.calendar.Location;
import destiny.core.calendar.TimeTools;
import kotlin.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.chrono.ChronoLocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <pre>
 * 計算兩星呈現某交角的時間
 * Swiss ephemeris 的實作是 RelativeTransitImpl
 * </pre> 
 */
public interface IRelativeTransit {

  /**
   * <pre>
   * 計算兩星下一個/上一個交角。
   * 注意！angle 有方向性，如果算相刑的角度，別忘了另外算 270度
   * TODO : 目前 RelativeTransitImpl 僅支援 Planet 以及 Asteroid
   * 傳回的 Time 是 GMT julDay
   * </pre>
   */
  @Nullable
  Double getRelativeTransit(Star transitStar , Star relativeStar , double angle , double gmtJulDay , boolean isForward);

  @Nullable
  default ChronoLocalDateTime getRelativeTransit(Star transitStar , Star relativeStar , double angle , ChronoLocalDateTime fromGmt , boolean isForward , Function<Double , ChronoLocalDateTime> revJulDayFunc) {
    double gmtJulDay = TimeTools.getGmtJulDay(fromGmt);
    Double value = getRelativeTransit(transitStar , relativeStar , angle , gmtJulDay , isForward);
    if (value != null)
      return revJulDayFunc.apply(value);
    else
      return null;
  }


  /**
   * 從 fromGmt 到 toGmt 之間，transitStar 對 relativeStar 形成 angle 交角的時間
   * @return 傳回一連串的 gmtJulDays
   */
  default List<Double> getPeriodRelativeTransitGmtJulDays(Star transitStar , Star relativeStar , double fromJulDay , double toJulDay , double angle) {
    List<Double> resultList = new ArrayList<>();
    while (fromJulDay < toJulDay) {
      Double value = getRelativeTransit(transitStar, relativeStar, angle, fromJulDay, true);
      if (value != null) {
        fromJulDay = value;
        if (fromJulDay > toJulDay)
          break;

        resultList.add(value);
        fromJulDay = fromJulDay + 0.000001;
      }
    }
    return resultList;
  }

  /**
   * 從 fromGmt 到 toGmt 之間，transitStar 對 relativeStar 形成 angle 交角的時間
   * @return List < Map < angle , Time > >
   * 傳回的是 GMT 時刻
   */
  @NotNull
  default List<ChronoLocalDateTime> getPeriodRelativeTransitGMTs(Star transitStar , Star relativeStar , double fromJulDay , double toJulDay , double angle , Function<Double , ChronoLocalDateTime> revJulDayFunc) {
    return getPeriodRelativeTransitGmtJulDays(transitStar , relativeStar , fromJulDay , toJulDay , angle)
      .stream()
      .map(revJulDayFunc)
      .collect(Collectors.toList());
  }

  /** 傳回 GMT */
  @NotNull
  default List<ChronoLocalDateTime> getPeriodRelativeTransitGMTs(Star transitStar , Star relativeStar , ChronoLocalDateTime fromGmt, ChronoLocalDateTime toGmt, double angle , Function<Double , ChronoLocalDateTime> revJulDayFunc) {
    double fromGmtJulDay = TimeTools.getGmtJulDay(fromGmt);
    double toGmtJulDay = TimeTools.getGmtJulDay(toGmt);
    return getPeriodRelativeTransitGMTs(transitStar ,relativeStar , fromGmtJulDay , toGmtJulDay , angle , revJulDayFunc);
  }


  /** 承上 , LMT 的 ChronoLocalDateTime 版本 */
  default List<ChronoLocalDateTime> getPeriodRelativeTransitLMTs(Star transitStar, Star relativeStar, ChronoLocalDateTime fromLmt, ChronoLocalDateTime toLmt, Location location, double angle, Function<Double, ChronoLocalDateTime> revJulDayFunc)  {
    ChronoLocalDateTime fromGmt = TimeTools.getGmtFromLmt(fromLmt , location);
    ChronoLocalDateTime   toGmt = TimeTools.getGmtFromLmt(  toLmt , location);

    return getPeriodRelativeTransitGmtJulDays(transitStar , relativeStar , TimeTools.getGmtJulDay(fromGmt) , TimeTools.getGmtJulDay(toGmt) , angle)
      .stream()
      .map(gmtJulDay -> {
        ChronoLocalDateTime gmt = revJulDayFunc.apply(gmtJulDay);
        return TimeTools.getLmtFromGmt(gmt , location);
      }).collect(Collectors.toList());
  }


  /**
   * 求出 fromStar 下一次/上一次 與 relativeStar 形成 angles[] 的角度 , 最近的是哪一次
   *
   * result 有可能為 empty , 例如計算 太陽/水星 [90,180,270] 的度數，將不會有結果
   *
   * @return 傳回的 Tuple , 前者為 GMT 時間，後者為角度
   */
  @Nullable
  default Pair<Double , Double> getNearestRelativeTransitGmtJulDay(Star transitStar , Star relativeStar , double fromGmtJulDay , Collection<Double> angles , boolean isForward ) {
    /**
     * 相交 270 度也算 90 度
     * 相交 240 度也是 120 度
     * 所以要重算一次角度
     */

    Set<Double> realAngles = new HashSet<>();
    for (double angle : angles) {
      realAngles.add(angle);
      if (angle != 0)
        realAngles.add(360 - angle);
    }

    Double resultGmtJulDay = null;
    Double resultAngle = null;
    for (double angle : realAngles) {
      Double value = getRelativeTransit(transitStar, relativeStar, angle, fromGmtJulDay, isForward);
      //Optional<Double> temp = getRelativeTransit(transitStar, relativeStar, angle, fromGmtJulDay, isForward);

      if (resultGmtJulDay == null) {
        //resultGmtJulDay = temp.orElse(null);
        resultGmtJulDay = value;
        resultAngle = angle;
      }
      else {

        if (value != null) {
          //目前已經有一個結果，比較看看現在算出的，和之前的，哪個比較近
          if (isForward) {
            //順推
            if (value <= resultGmtJulDay) {
              resultGmtJulDay = value;
              resultAngle = angle;
            }
          }
          else {
            //逆推
            if (value > resultGmtJulDay) {
              resultGmtJulDay = value;
              resultAngle = angle;
            }
          }
        }
      }
    } // each realAngle

    if (resultAngle != null && resultAngle > 180)
      resultAngle = 360-resultAngle;
    if (resultGmtJulDay != null) {
      return new Pair<>(resultGmtJulDay, resultAngle);
    } else {
      return null;
    }
  }


  /**
   * 承上 , Date Time 版本
   * @return 傳回的 Pair , 前者為 GMT 時間，後者為角度
   * */
  default Pair<Double , Double> getNearestRelativeTransitGmtJulDay(Star transitStar , Star relativeStar , ChronoLocalDateTime fromGmt , Collection<Double> angles , boolean isForward ) {
    double gmtJulDay = TimeTools.getGmtJulDay(fromGmt);
    return getNearestRelativeTransitGmtJulDay(transitStar , relativeStar , gmtJulDay , angles , isForward);
  }

}
