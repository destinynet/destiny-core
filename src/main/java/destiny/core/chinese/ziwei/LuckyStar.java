/**
 * Created by smallufo on 2017-04-11.
 */
package destiny.core.chinese.ziwei;

import destiny.core.chinese.Branch;
import destiny.core.chinese.BranchTools;
import destiny.core.chinese.Stem;

import java.util.function.Function;

import static destiny.core.chinese.Branch.*;

/**
 * 八吉星
 */
@SuppressWarnings("Duplicates")
public final class LuckyStar extends ZStar {

  public final static LuckyStar 文昌 = new LuckyStar("文昌"); // 甲
  public final static LuckyStar 文曲 = new LuckyStar("文曲"); // 甲
  public final static LuckyStar 左輔 = new LuckyStar("左輔"); // 甲
  public final static LuckyStar 右弼 = new LuckyStar("右弼"); // 甲
  public final static LuckyStar 天魁 = new LuckyStar("天魁"); // 甲
  public final static LuckyStar 天鉞 = new LuckyStar("天鉞"); // 甲
  public final static LuckyStar 祿存 = new LuckyStar("祿存"); // 甲
  public final static LuckyStar 天馬 = new LuckyStar("天馬"); // 乙級星

  public final static LuckyStar[] values = {文昌, 文曲, 左輔, 右弼, 天魁, 天鉞, 祿存, 天馬};

  public LuckyStar(String nameKey) {
    super(nameKey, ZStar.class.getName() , nameKey+"_ABBR");
  }

  /** 文昌 : 時支 -> 地支 */
  public final static Function<Branch, Branch> fun文昌 = hour -> Branch.get(10 - hour.getIndex());

  /** 文曲 : 時支 -> 地支 */
  public final static Function<Branch, Branch> fun文曲 = hour -> Branch.get(hour.getIndex() + 4);

  /** 左輔 : 月支 -> 地支 */
  public final static Function<Branch, Branch> fun左輔 = month -> Branch.get(month.getIndex() + 2);

  /** 右弼 : 月支 -> 地支 */
  public final static Function<Branch, Branch> fun右弼 = month -> Branch.get(12 - month.getIndex());

  /** 天魁 : 年干 -> 地支 */
  public final static Function<Stem, Branch> fun天魁 = year -> {
    switch (year) {
      case 甲:case 戊: case 庚: return 丑;
      case 乙:case 己: return 子;
      case 丙:case 丁: return 亥;
      case 辛: return 午;
      case 壬: case 癸: return 卯;
      default: throw new AssertionError(year);
    }
  };

  /** 天鉞 : 年干 -> 地支 */
  public final static Function<Stem , Branch> fun天鉞 = year -> {
    switch (year) {
      case 甲: case 戊: case 庚: return 未;
      case 乙: case 己: return 申;
      case 丙: case 丁: return 酉;
      case 辛: return 寅;
      case 壬: case 癸: return 巳;
      default: throw new AssertionError(year);
    }
  };

  /** 祿存 : 年干 -> 地支 */
  public final static Function<Stem , Branch> fun祿存 = year -> {
    switch (year) {
      case 甲: return 寅;
      case 乙: return 卯;
      case 丙: case 戊: return 巳;
      case 丁: case 己: return 午;
      case 庚: return 申;
      case 辛: return 酉;
      case 壬: return 亥;
      case 癸: return 子;
      default: throw new AssertionError(year);
    }
  };

  /** 天馬(月的驛馬) : 月支 -> 地支 */
  public final static Function<Branch , Branch> fun天馬 = month -> {
    switch (BranchTools.trilogy(month)) {
      case 火: return 申;
      case 木: return 巳;
      case 水: return 寅;
      case 金: return 亥;
      default: throw new AssertionError(month);
    }
  };
}
