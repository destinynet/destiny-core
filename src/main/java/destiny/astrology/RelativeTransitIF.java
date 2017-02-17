/*
 * @author smallufo
 * @date 2004/10/29
 * @time 下午 09:57:05
 */
package destiny.astrology;

import destiny.core.calendar.Location;
import destiny.core.calendar.Time;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <pre>
 * 計算兩星呈現某交角的時間
 * Swiss ephemeris 的實作是 RelativeTransitImpl
 * </pre> 
 */
public interface RelativeTransitIF {

  Logger logger2 = LoggerFactory.getLogger(RelativeTransitIF.class);

  /**
   * <pre>
   * 計算兩星下一個/上一個交角。
   * 注意！angle 有方向性，如果算相刑的角度，別忘了另外算 270度
   * TODO : 目前 RelativeTransitImpl 僅支援 Planet 以及 Asteroid
   * 傳回的 Time 是 GMT julDay
   * </pre>
   */
  Optional<Double> getRelativeTransit(Star transitStar , Star relativeStar , double angle , double gmtJulDay , boolean isForward);

  default Optional<LocalDateTime> getRelativeTransitGmt(Star transitStar , Star relativeStar , double angle , double gmtJulDay , boolean isForward) {
    return getRelativeTransit(transitStar , relativeStar , angle , gmtJulDay , isForward)
      .map(gmt_JulDay -> new Time(gmt_JulDay).toLocalDateTime());
  }

  default Optional<LocalDateTime> getRelativeTransit(Star transitStar , Star relativeStar , double angle , LocalDateTime fromGmt , boolean isForward) {
    double gmtJulDay = Time.getGmtJulDay(fromGmt);
    return getRelativeTransitGmt(transitStar , relativeStar , angle , gmtJulDay , isForward);
  }

  /**
   * 從 fromGmt 到 toGmt 之間，transitStar 對 relativeStar 形成 angle 交角的時間
   * @return List < Map < angle , Time > >
   * 傳回的是 GMT 時刻
   */
  @NotNull
  default List<Time> getPeriodRelativeTransitTimes(Star transitStar , Star relativeStar , double fromJulDay , double toJulDay , double angle) {
    List<Time> resultList = new ArrayList<>();
    while (fromJulDay < toJulDay)
    {
      Optional<Double> timeOptional = getRelativeTransit( transitStar , relativeStar , angle , fromJulDay , true);
      if (timeOptional.isPresent()) {
        fromJulDay = timeOptional.get();
        if (fromJulDay > toJulDay)
          break;
        resultList.add(new Time(timeOptional.get()));
        fromJulDay = fromJulDay +0.000001;
      }
    }
    return resultList;
  }

  @NotNull
  default List<LocalDateTime> getPeriodRelativeTransitLDTs(Star transitStar , Star relativeStar , double fromJulDay , double toJulDay , double angle) {
    List<LocalDateTime> resultList = new ArrayList<>();
    while (fromJulDay < toJulDay)
    {
      Optional<Double> timeOptional = getRelativeTransit( transitStar , relativeStar , angle , fromJulDay , true);
      if (timeOptional.isPresent()) {
        fromJulDay = timeOptional.get();
        if (fromJulDay > toJulDay)
          break;
        resultList.add(new Time(timeOptional.get()).toLocalDateTime());
        fromJulDay = fromJulDay +0.000001;
      }
    }
    return resultList;
  }

  @NotNull
  default List<LocalDateTime> getPeriodRelativeTransitLDTs(Star transitStar , Star relativeStar , LocalDateTime fromGmt, LocalDateTime toGmt, double angle) {
    double fromGmtJulDay = Time.getGmtJulDay(fromGmt);
    double toGmtJulDay = Time.getGmtJulDay(toGmt);
    return getPeriodRelativeTransitLDTs(transitStar ,relativeStar , fromGmtJulDay , toGmtJulDay , angle);
  }


  @NotNull
  default List<Time> getPeriodRelativeTransitTimes(Star transitStar , Star relativeStar , @NotNull Time fromGmt, @NotNull Time toGmt, double angle) {
    return getPeriodRelativeTransitTimes(transitStar ,relativeStar ,fromGmt.getGmtJulDay() , toGmt.getGmtJulDay() , angle);
  }

  /** 承上，此為計算 LMT , 回傳也是 LMT */
  @NotNull
  default List<Time> getLocalPeriodRelativeTransitTimes(Star transitStar , Star relativeStar , @NotNull Time fromLmt, @NotNull Time toLmt, Location location , double angle) {
    Time fromGmt = Time.getGMTfromLMT(fromLmt , location );
    Time   toGmt = Time.getGMTfromLMT(toLmt , location);
    return getPeriodRelativeTransitTimes(transitStar , relativeStar , fromGmt , toGmt , angle).stream().map(
      gmt -> Time.getLMTfromGMT(gmt , location)
    ).collect(Collectors.toList());
  }

  /** 承上 , LMT 的 LocalDateTime 版本 */
  default List<LocalDateTime> getLocalPeriodRelativeTransitTimes(Star transitStar , Star relativeStar , LocalDateTime fromLmt, LocalDateTime toLmt, Location location , double angle)  {
    LocalDateTime fromGmt = Time.getGmtFromLmt(fromLmt , location);
    LocalDateTime   toGmt = Time.getGmtFromLmt(  toLmt , location);
    return getPeriodRelativeTransitLDTs(transitStar , relativeStar , fromGmt , toGmt , angle).stream().map(
      gmt -> Time.getLmtFromGmt(gmt , location)
    ).collect(Collectors.toList());
  }


  /**
   * 求出 fromStar 下一次/上一次 與 relativeStar 形成 angles[] 的角度 , 最近的是哪一次
   *
   * 傳回的 pair , 左邊為 GMT 時間，右邊為角度
   *
   */
  default Optional<Pair<Double , Double>> getNearestRelativeTransitGmtJulDay(Star transitStar , Star relativeStar , double fromGmtJulDay , Collection<Double> angles , boolean isForward ) {
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
      Optional<Double> temp = getRelativeTransit(transitStar, relativeStar, angle, fromGmtJulDay, isForward);

      if (resultGmtJulDay == null) {
        resultGmtJulDay = temp.orElse(null);
        resultAngle = angle;
      }
      else {

        if (temp.isPresent()) {
          Double t = temp.get();
          //目前已經有一個結果，比較看看現在算出的，和之前的，哪個比較近
          if (isForward) {
            //順推
            if (t <= resultGmtJulDay) {
              resultGmtJulDay = t;
              resultAngle = angle;
            }
          }
          else {
            //逆推
            if (t > resultGmtJulDay) {
              resultGmtJulDay = t;
              resultAngle = angle;
            }
          }
        }
      }
    } // each realAngle

    if (resultAngle != null && resultAngle > 180)
      resultAngle = 360-resultAngle;
    if (resultGmtJulDay != null) {
      return Optional.of(ImmutablePair.of(resultGmtJulDay, resultAngle));
    } else {
      return Optional.empty();
    }
  }

  /**
   * 求出 fromStar 下一次/上一次 與 relativeStar 形成 angles[] 的角度 , 最近的是哪一次
   * 承上 , LocalDateTime 版本
   */
  default Optional<Pair<LocalDateTime , Double>> getNearestRelativeTransitTime(Star transitStar , Star relativeStar , double fromGmtJulDay , Collection<Double> angles , boolean isForward ) {
    Optional<Pair<Double , Double>> optionalPair= getNearestRelativeTransitGmtJulDay(transitStar , relativeStar , fromGmtJulDay , angles , isForward);
    return optionalPair.map(pair -> Pair.of(new Time(pair.getLeft()).toLocalDateTime() , pair.getRight()));
  }

  default Optional<Pair<LocalDateTime , Double>> getNearestRelativeTransitTime(Star transitStar , Star relativeStar , LocalDateTime gmt , Collection<Double> angles , boolean isForward ) {
    double gmtJulDay = Time.getGmtJulDay(gmt);
    Optional<Pair<Double , Double>> optionalPair= getNearestRelativeTransitGmtJulDay(transitStar , relativeStar , gmtJulDay , angles , isForward);
    return optionalPair.map(pair -> Pair.of(new Time(pair.getLeft()).toLocalDateTime() , pair.getRight()));
  }



}
