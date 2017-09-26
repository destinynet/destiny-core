/**
 * @author smallufo
 * Created on 2006/6/30 at 下午 11:13:01
 */
package destiny.core.calendar.chinese;

import destiny.core.Descriptive;
import destiny.core.calendar.Location;
import destiny.core.calendar.Time;
import destiny.core.calendar.eightwords.DayIF;
import destiny.core.calendar.eightwords.HourIF;
import destiny.core.calendar.eightwords.MidnightIF;
import destiny.core.chinese.StemBranch;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;

/**
 * 從 Time(LMT) / Location 取得 ChineseDate
 */
public interface ChineseDateIF extends Descriptive {

  Logger logger = LoggerFactory.getLogger(ChineseDateIF.class);

  // =============== 陽曆轉陰曆 ===============

  /**
   *
   * @param year proleptic year , 可能 <= 0
   */
  @NotNull
  ChineseDate getChineseDate(int year , int month , int day);

  default ChineseDate getChineseDate(LocalDate localDate) {
    return getChineseDate(localDate.getYear() , localDate.getMonthValue() , localDate.getDayOfMonth());
  }

  default ChineseDate getChineseDate(ChronoLocalDate localDate) {
    return getChineseDate(localDate.get(ChronoField.YEAR) , localDate.get(ChronoField.MONTH_OF_YEAR) , localDate.get(ChronoField.DAY_OF_MONTH));
  }

  /**
   * 最完整的「陽曆轉陰曆」演算法
   * 必須另外帶入 地點、日干支紀算法、時辰劃分法、子正計算方式、是否子初換日 5個參數
   */
  ChineseDate getChineseDate(LocalDateTime lmt , Location location , DayIF dayImpl , HourIF hourImpl , MidnightIF midnightImpl , boolean changeDayAfterZi);

  default ChineseDate getChineseDate(Time lmt , Location location , DayIF dayImpl , HourIF hourImpl , MidnightIF midnightImpl ,  boolean changeDayAfterZi) {
    return getChineseDate(lmt.toLocalDateTime() , location , dayImpl , hourImpl , midnightImpl , changeDayAfterZi);
  }

  // =============== 陰曆轉陽曆 ===============
  LocalDate getYangDate(int cycle, StemBranch year, boolean leap, int month, int day);

  default LocalDate getYangDate(ChineseDate cdate) {
    return getYangDate(cdate.getCycle() , cdate.getYear() , cdate.isLeapMonth() , cdate.getMonth() , cdate.getDay());
  }


  // =============== 日期操作 ===============

  /**
   * @param days 下n日，若 n = 0 , 則傳回自己
   *             若 n = -1 , 則傳回昨天
   */
  ChineseDate plusDays(ChineseDate chineseDate , int days);

  /**
   * 承上，往回推算
   */
  default ChineseDate minusDays(ChineseDate chineseDate , int days) {
    return plusDays(chineseDate , -days);
  }


  /** 傳回「下個月」的初一 */
  ChineseDate nextMonthStart(ChineseDate chineseDate);


  /** 傳回「上個月」的初一 */
  ChineseDate prevMonthStart(ChineseDate chineseDate);

  /** 列出該年所有月份(以及是否是閏月) , 可能傳回 12 or 13月 (有閏月的話) */
  default List<Tuple2<Integer , Boolean>> getMonthsOf(int cycle , StemBranch year) {
    List<Tuple2<Integer , Boolean>> list = new ArrayList<>(13);
    ChineseDate date = new ChineseDate(cycle , year , 1 , false , 1);

    while(date.getYear() == year) {
      list.add(Tuple.tuple(date.getMonth() , date.isLeapMonth()));
      date = nextMonthStart(date);
    }
    return list;
  }

  /** 列出該月有幾日 */
  default int getDaysOf(int cycle , StemBranch year , int month , boolean leap) {
    // 以當月初一開始
    ChineseDate date = new ChineseDate(cycle , year , month , leap , 1);
    // 推算下個月初一
    ChineseDate nextMonthStart = nextMonthStart(date);
    // 再往前推算一日
    ChineseDate monthEnd = minusDays(nextMonthStart , 1);
    return monthEnd.getDay();
  }
}
