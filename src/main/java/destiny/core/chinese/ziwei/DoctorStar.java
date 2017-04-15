/**
 * Created by smallufo on 2017-04-15.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Gender;
import destiny.core.chinese.Branch;
import destiny.core.chinese.Stem;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple3;

import java.util.function.BiFunction;
import java.util.function.Function;

import static destiny.core.chinese.ziwei.LuckyStar.fun祿存;

/**
 * 博士12神煞 , 丙級星
 *
 * 博士永遠跟著祿存走，也就是說 {@link LuckyStar#祿存} 的旁邊一定有博士，然後分別排列出力士、青龍、小耗……等星。
 * 陽男陰女順排，陰男陽女逆排。
 *
 * 亦即，這是 (年干,性別) -> 地支
 * 每次 function 都要 call {@link LuckyStar#fun祿存}
 *
 * TODO : 博士12神煞，是否要隨流年變化
 */
public class DoctorStar extends ZStar {

  public final static DoctorStar 博士 = new DoctorStar("博士");
  public final static DoctorStar 力士 = new DoctorStar("力士");
  public final static DoctorStar 青龍 = new DoctorStar("青龍");
  public final static DoctorStar 小耗 = new DoctorStar("小耗");
  public final static DoctorStar 將軍 = new DoctorStar("將軍");
  public final static DoctorStar 奏書 = new DoctorStar("奏書");
  public final static DoctorStar 飛廉 = new DoctorStar("飛廉");
  public final static DoctorStar 喜神 = new DoctorStar("喜神");
  public final static DoctorStar 病符 = new DoctorStar("病符");
  public final static DoctorStar 大耗 = new DoctorStar("大耗");
  public final static DoctorStar 伏兵 = new DoctorStar("伏兵");
  public final static DoctorStar 官府 = new DoctorStar("官府");

  public final static DoctorStar[] values = {博士, 力士, 青龍, 小耗, 將軍, 奏書, 飛廉, 喜神, 病符, 大耗, 伏兵, 官府};

  public DoctorStar(String nameKey) {
    super(nameKey,  ZStar.class.getName());
  }

  private final static Function<Tuple3<Stem , Gender , Integer>, Branch> branchGender2Branch = (tuple3) -> {
    Stem yearStem = tuple3.v1();
    Branch 祿存地支 = fun祿存.apply(yearStem);
    Gender gender = tuple3.v2();
    int steps = tuple3.v3();

    if ((yearStem.getBooleanValue() && gender == Gender.男) || (!yearStem.getBooleanValue() && gender == Gender.女)) {
      // 陽男 陰女 順行
      return 祿存地支.next(steps-1);
    } else {
      // 陰男 陽女 逆行
      return 祿存地支.prev(steps-1);
    }
  };

  public final static BiFunction<Stem, Gender , Branch> fun博士 = (stem , gender) -> branchGender2Branch.apply(Tuple.tuple(stem , gender , 1));
  public final static BiFunction<Stem, Gender , Branch> fun力士 = (stem , gender) -> branchGender2Branch.apply(Tuple.tuple(stem , gender , 2));
  public final static BiFunction<Stem, Gender , Branch> fun青龍 = (stem , gender) -> branchGender2Branch.apply(Tuple.tuple(stem , gender , 3));
  public final static BiFunction<Stem, Gender , Branch> fun小耗 = (stem , gender) -> branchGender2Branch.apply(Tuple.tuple(stem , gender , 4));
  public final static BiFunction<Stem, Gender , Branch> fun將軍 = (stem , gender) -> branchGender2Branch.apply(Tuple.tuple(stem , gender , 5));
  public final static BiFunction<Stem, Gender , Branch> fun奏書 = (stem , gender) -> branchGender2Branch.apply(Tuple.tuple(stem , gender , 6));
  public final static BiFunction<Stem, Gender , Branch> fun飛廉 = (stem , gender) -> branchGender2Branch.apply(Tuple.tuple(stem , gender , 7));
  public final static BiFunction<Stem, Gender , Branch> fun喜神 = (stem , gender) -> branchGender2Branch.apply(Tuple.tuple(stem , gender , 8));
  public final static BiFunction<Stem, Gender , Branch> fun病符 = (stem , gender) -> branchGender2Branch.apply(Tuple.tuple(stem , gender , 9));
  public final static BiFunction<Stem, Gender , Branch> fun大耗 = (stem , gender) -> branchGender2Branch.apply(Tuple.tuple(stem , gender , 10));
  public final static BiFunction<Stem, Gender , Branch> fun伏兵 = (stem , gender) -> branchGender2Branch.apply(Tuple.tuple(stem , gender , 11));
  public final static BiFunction<Stem, Gender , Branch> fun官府 = (stem , gender) -> branchGender2Branch.apply(Tuple.tuple(stem , gender , 12));
}
