/**
 * Created by smallufo on 2017-05-08.
 */
package destiny.core.chinese.ziwei;

import destiny.core.calendar.TimeTools;
import destiny.core.calendar.chinese.ChineseDate;
import destiny.core.calendar.chinese.ChineseDateIF;
import destiny.core.chinese.*;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;

import java.io.Serializable;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 紫微斗數 雜項工具
 */
public class ZiweiTools implements Serializable {

  /**
   * 從「虛歲」轉換為哪些值
   * @param vage 虛歲
   * @param westBirthYear 西元出生年份
   */
  public static int getOutput(int vage , FortuneOutput output , int westBirthYear) {
    switch (output) {
      case 虛歲: return vage;
      case 實歲: return vage-1;
      case 西元: {
        int trueAge = vage-1; // 先轉成 實歲
        return westBirthYear+trueAge; // 再加上西元年份
      }
      case 民國: {
        int trueAge = vage-1; // 先轉成 實歲
        int westYear = westBirthYear + trueAge; // 再轉成西元
        return westYear-1911; // 再轉成民國
      }
      default: throw new AssertionError("Error : " + output);
    }
  }

  /** 列出此大限中，包含哪十個流年 (陰曆 cycle + 地支干支) , 並且「虛歲」各別是幾歲 ,  */
  public static List<Tuple3<Integer , StemBranch, Integer>> getYearsOfFlowBig(@NotNull Builder builder, ZContext context, Branch flowBig) {
    IBigRange bigRangeImpl = context.getBigRangeImpl();
    StemBranch birthYear = builder.getChineseDate().getYear();
    int birthCycle = builder.getChineseDate().getCycle();

    // 先求出，虛歲，是幾歲到幾歲
    Tuple2<Integer , Integer> range = bigRangeImpl.getRange(builder.getBranchHouseMap().get(flowBig) , builder.getSet() , birthYear.getStem() , builder.getGender() , FortuneOutput.虛歲 , context.getHouseSeqImpl())
      .map((d1 , d2) -> Tuple.tuple(d1.intValue() , d2.intValue()));

    // 再把虛歲轉換成干支
    return IntStream.rangeClosed(range.v1() , range.v2()).boxed().map(vAge -> {
      StemBranch sb = birthYear.next(vAge-1); // 虛歲 (vAge) 轉換為年 , 要減一 . 虛歲
      int cycle;
      if (sb.getIndex() >= birthYear.getIndex()) {
//        if (vAge <= 60) {
//          cycle = birthCycle;
//        } else if (vAge > 60 && vAge <= 120) {
//          cycle = birthCycle+1;
//        }
        cycle = birthCycle + (vAge-1)/ 60;
      } else {
//        if (vAge <= 60)  // 2~60歲
//          cycle = birthCycle+1;
//        else if (vAge > 60 && vAge <= 120) // 其實只會出現在 62~120歲 (跳過 61 , 因為干支 index 相同)
//          cycle = birthCycle+2;

        cycle = birthCycle + (vAge-1)/ 60 +1;
      }

      return Tuple.tuple(cycle , sb , vAge);
    }).collect(Collectors.toList());
  }

  /**
   * @param flowYear 流年
   * @return 此流年有哪些流月（月份＋是否閏月）
   */
  public static List<Tuple2<Integer , Boolean>> getMonthsOf(int cycle , StemBranch flowYear , ChineseDateIF chineseDateImpl) {

    return chineseDateImpl.getMonthsOf(cycle , flowYear);
  }

  /**
   * @param cycle     cycle
   * @param flowYear  流年
   * @param flowMonth 流月
   * @param leap      是否閏月
   * @return 該流月的日子 (陰曆＋陽曆＋干支）
   */
  public static List<Tuple3<ChineseDate , ChronoLocalDate , StemBranch>> getDaysOfMonth(ChineseDateIF chineseDateImpl, int cycle, StemBranch flowYear , Integer flowMonth , boolean leap) {
    int days = chineseDateImpl.getDaysOf(cycle , flowYear , flowMonth , leap);
    List<Tuple3<ChineseDate, ChronoLocalDate, StemBranch>> list = new ArrayList<>(days);
    for(int i=1 ; i <= days ; i++) {
      ChineseDate yinDate = new ChineseDate(cycle , flowYear , flowMonth , leap , i);

      ChronoLocalDate yangDate = chineseDateImpl.getYangDate(yinDate);
      int lmtJulDay = (int) ( TimeTools.getGmtJulDay(yangDate.atTime(LocalTime.MIDNIGHT))+0.5);
      int index = (lmtJulDay-11) % 60;
      StemBranch sb = StemBranch.get(index);
      list.add(Tuple.tuple(yinDate , yangDate , sb));
    }
    return list;
  }

  public static List<StemBranch> getHoursOfDay(StemBranch day) {
    return Arrays.stream(Branch.values()).map(b -> {
      Stem stem = StemBranchUtils.getHourStem(day.getStem() , b);
      return StemBranch.get(stem , b);
    }).collect(Collectors.toList());
  }
}
