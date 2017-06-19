/**
 * Created by smallufo on 2015-05-17.
 */
package destiny.core.chinese.onePalm;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import destiny.astrology.Coordinate;
import destiny.astrology.HouseSystem;
import destiny.core.Gender;
import destiny.core.calendar.Location;
import destiny.core.calendar.Time;
import destiny.core.calendar.chinese.ChineseDate;
import destiny.core.calendar.chinese.ChineseDateHour;
import destiny.core.calendar.chinese.ChineseDateIF;
import destiny.core.calendar.eightwords.DayIF;
import destiny.core.calendar.eightwords.HourIF;
import destiny.core.calendar.eightwords.MidnightIF;
import destiny.core.calendar.eightwords.RisingSignIF;
import destiny.core.chinese.Branch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 *
 */
public interface PalmIF {

  Logger logger = LoggerFactory.getLogger(PalmIF.class);

  /** 本命盤，已經預先計算命宮 */
  default Palm getPalm(Gender gender, Branch yearBranch, boolean leap, int month, int day, Branch hourBranch , PositiveIF positiveImpl , Branch rising) {
    int positive = positiveImpl.isPositive(gender , yearBranch) ? 1 : -1 ;

    int realMonth = month ;
    if (leap && day > 15)  // 若為閏月，15日以後算下個月
      realMonth++;

    // 年上起月
    Branch monthBranch = yearBranch.next((realMonth-1)*positive);

    // 月上起日
    Branch dayBranch = monthBranch.next((day-1)* positive);

    // 日上起時
    Branch hour = dayBranch.next((hourBranch.getIndex())* positive);

    BiMap<Branch, Palm.House> houseMap = HashBiMap.create(12);
    for(int i=0 ; i<12 ; i++) {
      houseMap.put(rising.next(i), Palm.House.values()[i]);
    }
    return new Palm(gender, yearBranch , monthBranch , dayBranch , hour , houseMap);
  }

  /** 本命盤 , 沒有預先計算命宮 */
  default Palm getPalm(Gender gender, Branch yearBranch, boolean leap, int month, int day, Branch hourBranch , PositiveIF positiveImpl) {
    int positive = positiveImpl.isPositive(gender , yearBranch) ? 1 : -1 ;

    logger.trace("positive = {}" , positive);

    int realMonth = month ;
    if (leap && day > 15)  // 若為閏月，15日以後算下個月
      realMonth++;

    logger.trace("yearBranch = {}", yearBranch);

    // 年上起月
    Branch monthBranch = yearBranch.next((realMonth-1)*positive);

    logger.trace("monthBranch = {}" , monthBranch);

    // 月上起日
    Branch dayBranch = monthBranch.next((day-1)* positive);
    logger.trace("dayBranch = {}" , dayBranch);

    // 日上起時
    Branch hour = dayBranch.next((hourBranch.getIndex())* positive);
    logger.trace("hour = {}" , hour);

    // 命宮
    int steps = Branch.卯.getAheadOf(hourBranch);
    Branch rising = hour.next(steps * positive);
    BiMap<Branch, Palm.House> houseMap = HashBiMap.create(12);
    for(int i=0 ; i<12 ; i++) {
      houseMap.put(rising.next(i), Palm.House.values()[i]);
    }
    return new Palm(gender, yearBranch , monthBranch , dayBranch , hour , houseMap);
  }

  /**
   * 本命盤
   * @param trueRising 是否已經預先計算好了真實的上升星座
   * */
  default Palm getPalm(Gender gender , ChineseDateHour chineseDateHour , PositiveIF positiveImpl , Optional<Branch> trueRising) {
    return trueRising
      .map  (branch -> getPalm(gender, chineseDateHour.getYear().getBranch(), chineseDateHour.isLeapMonth(), chineseDateHour.getMonth(), chineseDateHour.getDay(), chineseDateHour.getHourBranch(), positiveImpl, branch))
      .orElseGet(() -> getPalm(gender, chineseDateHour.getYear().getBranch(), chineseDateHour.isLeapMonth(), chineseDateHour.getMonth(), chineseDateHour.getDay(), chineseDateHour.getHourBranch(), positiveImpl));
  }

  /**
   * 本命盤：最完整的計算方式 , 包含時分秒、經緯度、時區
   * @param trueRisingSign 真實星體命宮. 若為 false , 則為傳統一掌經起命宮
   */
  default PalmWithMeta getPalmWithMeta(Gender gender, LocalDateTime lmt, Location loc, String place,
                                       PositiveIF positiveImpl, ChineseDateIF chineseDateImpl, DayIF dayImpl,
                                       HourIF hourImpl, MidnightIF midnightImpl,
                                       RisingSignIF risingSignImpl,
                                       boolean changeDayAfterZi, boolean trueRisingSign) {
    ChineseDate cDate = chineseDateImpl.getChineseDate(lmt , loc , dayImpl , hourImpl , midnightImpl , changeDayAfterZi);
    Branch hourBranch = hourImpl.getHour(lmt , loc);
    ChineseDateHour chineseDateHour = new ChineseDateHour(cDate , hourBranch);

    Optional<Branch> trueRising;
    if (trueRisingSign) {
      // 真實上升星座
      trueRising = Optional.of(risingSignImpl.getRisingSign(lmt , loc , HouseSystem.PLACIDUS , Coordinate.ECLIPTIC).getBranch());
    } else {
      trueRising = Optional.empty();
    }
    Palm palm = getPalm(gender , chineseDateHour , positiveImpl , trueRising);
    return new PalmWithMeta(palm , lmt , loc , place , chineseDateImpl , dayImpl , positiveImpl , hourImpl , midnightImpl , changeDayAfterZi, trueRisingSign);
  }

  default PalmWithMeta getPalmWithMeta(Gender gender , Time lmt , Location loc , String place , PositiveIF positiveImpl ,
                                       ChineseDateIF chineseDateImpl , DayIF dayImpl , HourIF hourImpl , MidnightIF midnightImpl ,
                                       RisingSignIF risingSignImpl,
                                       boolean changeDayAfterZi) {
    return getPalmWithMeta(gender , lmt.toLocalDateTime() , loc , place , positiveImpl , chineseDateImpl , dayImpl , hourImpl , midnightImpl , risingSignImpl, changeDayAfterZi, false);
  }

  /**
   * 大運從掌中年上起月，男順、女逆，輪數至本生月起運。本生月所在宮為一運，下一宮為二運，而一運管10年。
   *
   *
   * 女則逆行，也從子（天貴宮）上起正月，三月則落在戌（天藝宮）上，戌為初運（1－10歲），酉（天刃宮）為二運（11－20歲）也，餘此類推。
   *
   * 大運盤，每運10年，從 1歲起. 1~10 , 11~20 , 21~30 ...
   * Map 的 key 為「運的開始」: 1 , 11 , 21 , 31 ...
   * @param count : 要算多少組大運
   * */
  default Map<Integer , Branch> getMajorFortunes(Gender gender , Branch yearBranch , int month , int count) {
    int positive = (gender==Gender.男 ? 1 : -1) ;

    // 年上起月
    Branch monthBranch = yearBranch.next((month - 1) * positive);

    Map<Integer , Branch> map = new TreeMap<>();

    for(int i = 1 ; i <= count ; i++) {
      map.put((i-1)* 10 + 1 , monthBranch.next((i-1)*positive));
    }
    return map;
  }

}
