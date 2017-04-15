/**
 * Created by smallufo on 2017-04-11.
 */
package destiny.core.chinese.ziwei;

import destiny.astrology.DayNight;
import destiny.core.chinese.Branch;
import destiny.core.chinese.BranchTools;
import destiny.core.chinese.Stem;
import destiny.core.chinese.TianyiIF;
import org.jooq.lambda.function.Function2;

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
  public final static LuckyStar 天魁 = new LuckyStar("天魁"); // 甲 , 丙火 , 天乙貴人 , 陽貴
  public final static LuckyStar 天鉞 = new LuckyStar("天鉞"); // 甲 , 丁火 , 玉堂貴人 , 陰貴
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

  /** 天魁 (陽貴人) : 年干 -> 地支
   * 第一種算法： 「甲戊庚牛羊，乙己鼠猴郷，丙丁豬雞位，六辛為馬虎，壬癸兔蛇藏，此是貴人方。」
   * */
  public final static Function2<Stem, TianyiIF , Branch> fun天魁 = (year , tianyiImpl) -> tianyiImpl.getFirstTianyi(year , DayNight.DAY);

  /** 天鉞 (陰貴人) : 年干 -> 地支
   * 第一種算法： 「甲戊庚牛羊，乙己鼠猴郷，丙丁豬雞位，六辛為馬虎，壬癸兔蛇藏，此是貴人方。」
   * */
  public final static Function2<Stem , TianyiIF , Branch> fun天鉞 = (year , tianyiImpl) -> tianyiImpl.getFirstTianyi(year , DayNight.NIGHT);

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

  /** 天馬(月的驛馬) : 月支 -> 地支 . TODO : 也有「年」的版本 */
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
