/**
 * Created by smallufo on 2017-06-16.
 */
package destiny.core.chinese.ziwei;

import destiny.core.chinese.Branch;
import destiny.core.chinese.BranchTools;
import destiny.core.chinese.FiveElement;
import org.jooq.lambda.function.Function2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static destiny.core.chinese.Branch.*;

/**
 * 將前12星
 * <p>
 * 排列規則：
 * <p>
 * 按流年（生年）地支起將星，順行：
 * <p>
 * 流年將前諸星：
 * 寅午戍年將星午, 申子辰年子將星, 巳酉丑將酉上駐, 亥卯未將卯上停
 * 攀鞍歲驛並息神, 華蓋劫煞災煞輕, 天煞指背咸池續, 月煞亡神次第行
 */
public class StarGeneralFront extends ZStar {

  public final static StarGeneralFront 將星 = new StarGeneralFront("將星");
  public final static StarGeneralFront 攀鞍 = new StarGeneralFront("攀鞍");
  public final static StarGeneralFront 歲馹 = new StarGeneralFront("歲馹");
  public final static StarGeneralFront 息神 = new StarGeneralFront("息神");
  public final static StarGeneralFront 華蓋 = new StarGeneralFront("華蓋");
  public final static StarGeneralFront 劫煞 = new StarGeneralFront("劫煞");
  public final static StarGeneralFront 災煞 = new StarGeneralFront("災煞");
  public final static StarGeneralFront 天煞 = new StarGeneralFront("天煞");
  public final static StarGeneralFront 指背 = new StarGeneralFront("指背");
  public final static StarGeneralFront 咸池 = new StarGeneralFront("咸池");
  public final static StarGeneralFront 月煞 = new StarGeneralFront("月煞");
  public final static StarGeneralFront 亡神 = new StarGeneralFront("亡神");

  public final static StarGeneralFront[] values = {將星, 攀鞍, 歲馹, 息神, 華蓋, 劫煞, 災煞, 天煞, 指背, 咸池, 月煞, 亡神};

  private final static List<StarGeneralFront> list = Arrays.asList(values);

  public StarGeneralFront(String nameKey) {
    super(nameKey, StarGeneralFront.class.getName(), Type.將前);
  }

  private final static Function<FiveElement , Branch> func = (五行) -> {
    switch (五行) {
      case 火: return 午;
      case 木: return 卯;
      case 水: return 子;
      case 金: return 酉;
      default: throw new AssertionError("Error : " + 五行);
    }
  };

  private final static Function2<Branch , StarGeneralFront, Branch> fun = (年支 , 星) -> {
    FiveElement 五行 = BranchTools.INSTANCE.trilogy(年支);
    // 第一個 (將星)
    Branch head = func.apply(五行);
    int steps = list.indexOf(星);
    return head.next(steps);
  };

  public final static Function<Branch , Branch> fun將星 = (年支) -> fun.apply(年支 , 將星);
  public final static Function<Branch , Branch> fun攀鞍 = (年支) -> fun.apply(年支 , 攀鞍);
  public final static Function<Branch , Branch> fun歲馹 = (年支) -> fun.apply(年支 , 歲馹);
  public final static Function<Branch , Branch> fun息神 = (年支) -> fun.apply(年支 , 息神);
  public final static Function<Branch , Branch> fun華蓋 = (年支) -> fun.apply(年支 , 華蓋);
  public final static Function<Branch , Branch> fun劫煞 = (年支) -> fun.apply(年支 , 劫煞);
  public final static Function<Branch , Branch> fun災煞 = (年支) -> fun.apply(年支 , 災煞);
  public final static Function<Branch , Branch> fun天煞 = (年支) -> fun.apply(年支 , 天煞);
  public final static Function<Branch , Branch> fun指背 = (年支) -> fun.apply(年支 , 指背);
  public final static Function<Branch , Branch> fun咸池 = (年支) -> fun.apply(年支 , 咸池);
  public final static Function<Branch , Branch> fun月煞 = (年支) -> fun.apply(年支 , 月煞);
  public final static Function<Branch , Branch> fun亡神 = (年支) -> fun.apply(年支 , 亡神);

  public final static Map<StarGeneralFront , Function<Branch , Branch>> funMap = new HashMap<StarGeneralFront , Function<Branch, Branch>>() {{
    put(將星 , fun將星);
    put(攀鞍 , fun攀鞍);
    put(歲馹 , fun歲馹);
    put(息神 , fun息神);
    put(華蓋 , fun華蓋);
    put(劫煞 , fun劫煞);
    put(災煞 , fun災煞);
    put(天煞 , fun天煞);
    put(指背 , fun指背);
    put(咸池 , fun咸池);
    put(月煞 , fun月煞);
    put(亡神 , fun亡神);
  }};
}
