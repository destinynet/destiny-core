/**
 * Created by smallufo on 2017-04-19.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Gender;
import destiny.core.chinese.Branch;
import destiny.core.chinese.FiveElement;
import destiny.core.chinese.YinYangIF;
import org.jooq.lambda.function.Function3;
import org.jooq.lambda.function.Function4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static destiny.core.chinese.Branch.*;

/** 長生 12 神煞 */
public class StarLongevity extends ZStar {

  public final static StarLongevity 長生 = new StarLongevity("長生");
  public final static StarLongevity 沐浴 = new StarLongevity("沐浴");
  public final static StarLongevity 冠帶 = new StarLongevity("冠帶");
  public final static StarLongevity 臨官 = new StarLongevity("臨官");
  public final static StarLongevity 帝旺 = new StarLongevity("帝旺");
  public final static StarLongevity 衰   = new StarLongevity("衰");
  public final static StarLongevity 病   = new StarLongevity("病");
  public final static StarLongevity 死   = new StarLongevity("死");
  public final static StarLongevity 墓   = new StarLongevity("墓");
  public final static StarLongevity 絕   = new StarLongevity("絕");
  public final static StarLongevity 胎   = new StarLongevity("胎");
  public final static StarLongevity 養   = new StarLongevity("養");

  public final static StarLongevity[] VALUES = {長生, 沐浴, 冠帶, 臨官, 帝旺, 衰, 病, 死, 墓, 絕, 胎, 養};

  private final static List<StarLongevity> list = Arrays.asList(VALUES);

  public StarLongevity(String nameKey) {
    super(nameKey, ZStar.class.getName(), nameKey+"_ABBR", Type.長生);
  }

  private static Logger logger = LoggerFactory.getLogger(StarLongevity.class);

  private final static Function<FiveElement , Branch> func = (五行) -> {
    switch (五行) {
      case 水: return 申;
      case 木: return 亥;
      case 金: return 巳;
      case 火: return 寅;
      case 土: return 申; // 土水共長生
      default: throw new AssertionError("Error : " + 五行);
    }
  };

  public final static Function4<FiveElement , Gender, YinYangIF , StarLongevity , Branch> fun = (五行 , gender , 陰陽 , 星體) -> {
    // 第一個（長生）
    Branch head = func.apply(五行);

    int steps = list.indexOf(星體);
    if ( (gender == Gender.男 && 陰陽.getBooleanValue()) || (gender == Gender.女 && !陰陽.getBooleanValue())) {
      // 陽男 陰女 順行
      return head.next(steps);
    } else {
      // 陰男 陽女 逆行
      return head.prev(steps);
    }
  };

  public final static Function3<FiveElement , Gender, YinYangIF , Branch> fun長生 = (五行 , gender , 陰陽) -> fun.apply(五行 , gender , 陰陽 , 長生);
  public final static Function3<FiveElement , Gender, YinYangIF , Branch> fun沐浴 = (五行 , gender , 陰陽) -> fun.apply(五行 , gender , 陰陽 , 沐浴);
  public final static Function3<FiveElement , Gender, YinYangIF , Branch> fun冠帶 = (五行 , gender , 陰陽) -> fun.apply(五行 , gender , 陰陽 , 冠帶);
  public final static Function3<FiveElement , Gender, YinYangIF , Branch> fun臨官 = (五行 , gender , 陰陽) -> fun.apply(五行 , gender , 陰陽 , 臨官);
  public final static Function3<FiveElement , Gender, YinYangIF , Branch> fun帝旺 = (五行 , gender , 陰陽) -> fun.apply(五行 , gender , 陰陽 , 帝旺);

  public final static Function3<FiveElement , Gender, YinYangIF , Branch> fun衰   = (五行 , gender , 陰陽) -> fun.apply(五行 , gender , 陰陽 , 衰);
  public final static Function3<FiveElement , Gender, YinYangIF , Branch> fun病   = (五行 , gender , 陰陽) -> fun.apply(五行 , gender , 陰陽 , 病);
  public final static Function3<FiveElement , Gender, YinYangIF , Branch> fun死   = (五行 , gender , 陰陽) -> fun.apply(五行 , gender , 陰陽 , 死);
  public final static Function3<FiveElement , Gender, YinYangIF , Branch> fun墓   = (五行 , gender , 陰陽) -> fun.apply(五行 , gender , 陰陽 , 墓);
  public final static Function3<FiveElement , Gender, YinYangIF , Branch> fun絕   = (五行 , gender , 陰陽) -> fun.apply(五行 , gender , 陰陽 , 絕);
  public final static Function3<FiveElement , Gender, YinYangIF , Branch> fun胎   = (五行 , gender , 陰陽) -> fun.apply(五行 , gender , 陰陽 , 胎);
  public final static Function3<FiveElement , Gender, YinYangIF , Branch> fun養   = (五行 , gender , 陰陽) -> fun.apply(五行 , gender , 陰陽 , 養);

}
