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
import destiny.core.calendar.chinese.ChineseDate;
import destiny.core.calendar.chinese.ChineseDateHour;
import destiny.core.calendar.chinese.IChineseDate;
import destiny.core.calendar.chinese.IFinalMonthNumber;
import destiny.core.calendar.chinese.IFinalMonthNumber.MonthAlgo;
import destiny.core.calendar.eightwords.*;
import destiny.core.chinese.Branch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.chrono.ChronoLocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 *
 */
public interface PalmIF {

  Logger logger = LoggerFactory.getLogger(PalmIF.class);

  /**
   * 本命盤，已經預先計算命宮
   * @param clockwiseHouse 宮位飛佈，順時針(true) or 逆時針(false)
   * */
  default Palm getPalm(Gender gender, Branch yearBranch, boolean leap, int monthNum, int dayNum, Branch hourBranch , PositiveIF positiveImpl , Branch rising , Branch monthBranch , MonthAlgo monthAlgo , boolean clockwiseHouse) {
    int positive = positiveImpl.isPositive(gender , yearBranch) ? 1 : -1 ;

    int finalMonthNum = IFinalMonthNumber.Companion.getFinalMonthNumber(monthNum , leap , monthBranch , dayNum , monthAlgo);

    // 年上起月
    Branch month = yearBranch.next((finalMonthNum-1)*positive);

    // 月上起日
    Branch day = month.next((dayNum-1)* positive);

    // 日上起時
    Branch hour = day.next((hourBranch.getIndex())* positive);

    BiMap<Branch, Palm.House> houseMap = HashBiMap.create(12);
    for(int i=0 ; i<12 ; i++) {
      houseMap.put( clockwiseHouse ? rising.next(i) : rising.prev(i), Palm.House.values()[i]);
    }
    return new Palm(gender, yearBranch , month , day , hour , houseMap);
  }

  /**
   * 本命盤 , 沒有預先計算命宮
   * @param clockwiseHouse 宮位飛佈，順時針(true) or 逆時針(false)
   * */
  default Palm getPalm(Gender gender, Branch yearBranch, boolean leap, int monthNum, int dayNum, Branch hourBranch, PositiveIF positiveImpl, Branch monthBranch , MonthAlgo monthAlgo , boolean clockwiseHouse) {
    int positive = positiveImpl.isPositive(gender , yearBranch) ? 1 : -1 ;

    logger.trace("positive = {}" , positive);

    int finalMonthNum = IFinalMonthNumber.Companion.getFinalMonthNumber(monthNum , leap , monthBranch , dayNum , monthAlgo);

//    int finalMonthNum = monthNum ;
//    if (leap && dayNum > 15)  // 若為閏月，15日以後算下個月
//      finalMonthNum++;

    logger.trace("yearBranch = {}", yearBranch);

    // 年上起月
    Branch month = yearBranch.next((finalMonthNum-1)*positive);

    // 月上起日
    Branch day = month.next((dayNum-1)* positive);

    // 日上起時
    Branch hour = day.next((hourBranch.getIndex())* positive);

    // 命宮
    int steps = Branch.卯.getAheadOf(hourBranch);
    Branch rising = hour.next(steps * positive);
    BiMap<Branch, Palm.House> houseMap = HashBiMap.create(12);
    for(int i=0 ; i<12 ; i++) {
      houseMap.put(clockwiseHouse ? rising.next(i) : rising.prev(i), Palm.House.values()[i]);
    }
    return new Palm(gender, yearBranch , month , day , hour , houseMap);
  }

  /** 沒帶入節氣資料 , 內定把月份計算採用 {@link MonthAlgo#MONTH_LEAP_SPLIT15} 的演算法 */
  default Palm getPalm(Gender gender, Branch yearBranch, boolean leap, int monthNum, int dayNum, Branch hourBranch, PositiveIF positiveImpl, boolean clockwiseHouse) {
    int positive = positiveImpl.isPositive(gender , yearBranch) ? 1 : -1 ;

    logger.trace("positive = {}" , positive);

    int finalMonthNum = monthNum ;
    if (leap && dayNum > 15)  // 若為閏月，15日以後算下個月
      finalMonthNum++;

    logger.trace("yearBranch = {}", yearBranch);

    // 年上起月
    Branch month = yearBranch.next((finalMonthNum-1)*positive);

    // 月上起日
    Branch day = month.next((dayNum-1)* positive);

    // 日上起時
    Branch hour = day.next((hourBranch.getIndex())* positive);

    // 命宮
    int steps = Branch.卯.getAheadOf(hourBranch);
    Branch rising = hour.next(steps * positive);
    BiMap<Branch, Palm.House> houseMap = HashBiMap.create(12);
    for(int i=0 ; i<12 ; i++) {
      houseMap.put(clockwiseHouse ? rising.next(i) : rising.prev(i), Palm.House.values()[i]);
    }
    return new Palm(gender, yearBranch , month , day , hour , houseMap);
  }

  /**
   * 本命盤
   *
   * @param trueRising  是否已經預先計算好了真實的上升星座
   * @param monthBranch 「節氣」的月支
   */
  default Palm getPalm(Gender gender , ChineseDateHour chineseDateHour , PositiveIF positiveImpl , Optional<Branch> trueRising , Branch monthBranch , MonthAlgo monthAlgo , boolean clockwiseHouse) {
    return trueRising
      .map  (rising -> getPalm(gender, chineseDateHour.getYear().getBranch(), chineseDateHour.isLeapMonth(), chineseDateHour.getMonth(), chineseDateHour.getDay(), chineseDateHour.getHourBranch(), positiveImpl, rising , monthBranch, monthAlgo, clockwiseHouse))
      .orElseGet(() -> getPalm(gender, chineseDateHour.getYear().getBranch(), chineseDateHour.isLeapMonth(), chineseDateHour.getMonth(), chineseDateHour.getDay(), chineseDateHour.getHourBranch(), positiveImpl,          monthBranch, monthAlgo , clockwiseHouse));
  }

  /**
   * 本命盤：最完整的計算方式 , 包含時分秒、經緯度、時區
   * @param yearMonthImpl   八字年月的計算實作（主要是用於計算月令）
   * @param trueRisingSign  真實星體命宮. 若為 false , 則為傳統一掌經起命宮
   * @param clockwiseHouse  宮位飛佈，順時針(true) or 逆時針(false)
   */
  default PalmWithMeta getPalmWithMeta(Gender gender, ChronoLocalDateTime lmt, Location loc, String place, PositiveIF positiveImpl,
                                       IChineseDate chineseDateImpl, IDay dayImpl, IHour hourImpl, IMidnight midnightImpl,
                                       IRisingSign risingSignImpl, IYearMonth yearMonthImpl, MonthAlgo monthAlgo,
                                       boolean changeDayAfterZi, boolean trueRisingSign, boolean clockwiseHouse) {
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

    // 節氣的月支
    Branch monthBranch = yearMonthImpl.getMonth(lmt , loc).getBranch();
    Palm palm = getPalm(gender , chineseDateHour , positiveImpl , trueRising , monthBranch, monthAlgo , clockwiseHouse);
    return new PalmWithMeta(palm , lmt , loc , place , chineseDateImpl , dayImpl , positiveImpl , hourImpl , midnightImpl , changeDayAfterZi, trueRisingSign, monthAlgo);
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
