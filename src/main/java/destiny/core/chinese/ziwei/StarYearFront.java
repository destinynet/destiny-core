/**
 * Created by smallufo on 2017-06-17.
 */
package destiny.core.chinese.ziwei;

import destiny.core.chinese.Branch;
import org.jooq.lambda.function.Function2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 歲前 12 星
 */
public class StarYearFront extends ZStar {

  public final static StarYearFront 歲建 = new StarYearFront("歲建");
  public final static StarYearFront 晦氣 = new StarYearFront("晦氣");
  public final static StarYearFront 喪門 = new StarYearFront("喪門");
  public final static StarYearFront 貫索 = new StarYearFront("貫索");
  public final static StarYearFront 官符 = new StarYearFront("官符");
  public final static StarYearFront 小耗 = new StarYearFront("小耗");
  public final static StarYearFront 歲破 = new StarYearFront("歲破");
  public final static StarYearFront 龍德 = new StarYearFront("龍德");
  public final static StarYearFront 白虎 = new StarYearFront("白虎");
  public final static StarYearFront 天德 = new StarYearFront("天德");
  public final static StarYearFront 吊客 = new StarYearFront("吊客");
  public final static StarYearFront 病符 = new StarYearFront("病符");

  public final static StarYearFront[] values = {歲建, 晦氣, 喪門, 貫索, 官符, 小耗, 歲破, 龍德, 白虎, 天德, 吊客, 病符};

  private final static List<StarYearFront> list = Arrays.asList(values);

  public StarYearFront(String nameKey) {
    super(nameKey, StarYearFront.class.getName(), Type.歲前);
  }

  private final static Function2<Branch , StarYearFront , Branch> fun = (年支 , 星) -> {
    int steps = list.indexOf(星);
    return 年支.next(steps);
  };

  public final static Function<Branch , Branch> fun歲建 = (年支) -> fun.apply(年支 , 歲建);
  public final static Function<Branch , Branch> fun晦氣 = (年支) -> fun.apply(年支 , 晦氣);
  public final static Function<Branch , Branch> fun喪門 = (年支) -> fun.apply(年支 , 喪門);
  public final static Function<Branch , Branch> fun貫索 = (年支) -> fun.apply(年支 , 貫索);
  public final static Function<Branch , Branch> fun官符 = (年支) -> fun.apply(年支 , 官符);
  public final static Function<Branch , Branch> fun小耗 = (年支) -> fun.apply(年支 , 小耗);
  public final static Function<Branch , Branch> fun歲破 = (年支) -> fun.apply(年支 , 歲破);
  public final static Function<Branch , Branch> fun龍德 = (年支) -> fun.apply(年支 , 龍德);
  public final static Function<Branch , Branch> fun白虎 = (年支) -> fun.apply(年支 , 白虎);
  public final static Function<Branch , Branch> fun天德 = (年支) -> fun.apply(年支 , 天德);
  public final static Function<Branch , Branch> fun吊客 = (年支) -> fun.apply(年支 , 吊客);
  public final static Function<Branch , Branch> fun病符 = (年支) -> fun.apply(年支 , 病符);

  public final static Map<StarYearFront , Function<Branch , Branch>> funMap = new HashMap<StarYearFront , Function<Branch, Branch>>() {{
    put(歲建 , fun歲建);
    put(晦氣 , fun晦氣);
    put(喪門 , fun喪門);
    put(貫索 , fun貫索);
    put(官符 , fun官符);
    put(小耗 , fun小耗);
    put(歲破 , fun歲破);
    put(龍德 , fun龍德);
    put(白虎 , fun白虎);
    put(天德 , fun天德);
    put(吊客 , fun吊客);
    put(病符 , fun病符);
  }};
}
