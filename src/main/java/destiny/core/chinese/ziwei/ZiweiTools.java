/**
 * Created by smallufo on 2017-05-08.
 */
package destiny.core.chinese.ziwei;

import destiny.core.calendar.chinese.ChineseDate;
import destiny.core.chinese.Branch;
import destiny.core.chinese.FortuneOutput;
import destiny.core.chinese.StemBranch;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 紫微斗數 雜項工具
 */
public class ZiweiTools implements Serializable {

  /** 列出此大限中，包含哪十個流年 , 並且「虛歲」各別是幾歲 */
  public static List<Tuple2<StemBranch, Integer>> getYearsOfFlowBig(@NotNull Builder builder, ZContext context, Branch flowBig) {
    IBigRange bigRangeImpl = context.getBigRangeImpl();
    StemBranch birthYear = builder.getChineseDate().getYear();

    // 先求出，虛歲，是幾歲到幾歲
    Tuple2<Integer , Integer> range = bigRangeImpl.getRange(builder.getBranchHouseMap().get(flowBig) , builder.getSet() , birthYear.getStem() , builder.getGender() , FortuneOutput.虛歲 , context.getHouseSeqImpl())
      .map((d1 , d2) -> Tuple.tuple(d1.intValue() , d2.intValue()));

    // 再把虛歲轉換成干支
    return IntStream.rangeClosed(range.v1() , range.v2()).boxed().map(age -> {
      StemBranch sb = birthYear.next(age-1); // 虛歲要減一
      return Tuple.tuple(sb , age);
    }).collect(Collectors.toList());
  }

  /**
   * @param flowYear  流年
   * @param flowMonth 流月
   * @return  該流月的日子
   */
  public static List<Tuple3<StemBranch , Integer , Boolean>> getDaysOfMonth(@NotNull Builder builder , ZContext context ,
                                                                            StemBranch flowYear , Branch flowMonth) {
    ChineseDate cDate = builder.getChineseDate();
    return new ArrayList<>();
  }
}
