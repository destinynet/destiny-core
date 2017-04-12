/**
 * Created by smallufo on 2017-04-12.
 */
package destiny.core.chinese.ziwei;

import destiny.core.chinese.Branch;
import destiny.core.chinese.BranchTools;
import destiny.core.chinese.Stem;
import org.jooq.lambda.function.Function3;

import java.util.function.BiFunction;
import java.util.function.Function;

import static destiny.core.chinese.Branch.*;
import static destiny.core.chinese.BranchTools.direction;
import static destiny.core.chinese.BranchTools.trilogy;
import static destiny.core.chinese.ziwei.LuckyStar.*;

/**
 * 乙級星有總共有32顆
 */
@SuppressWarnings("Duplicates")
public class MinorStar extends ZStar {

  public final static MinorStar 天官 = new MinorStar("天官");
  public final static MinorStar 天福 = new MinorStar("天福");
  public final static MinorStar 天廚 = new MinorStar("天廚");
  public final static MinorStar 天刑 = new MinorStar("天刑");
  public final static MinorStar 天姚 = new MinorStar("天姚");
  public final static MinorStar 解神 = new MinorStar("解神");
  public final static MinorStar 天巫 = new MinorStar("天巫");
  public final static MinorStar 天月 = new MinorStar("天月");
  public final static MinorStar 陰煞 = new MinorStar("陰煞");
  public final static MinorStar 台輔 = new MinorStar("台輔");
  public final static MinorStar 封誥 = new MinorStar("封誥");
  public final static MinorStar 天空 = new MinorStar("天空");
  public final static MinorStar 天哭 = new MinorStar("天哭");
  public final static MinorStar 天虛 = new MinorStar("天虛");
  public final static MinorStar 龍池 = new MinorStar("龍池");
  public final static MinorStar 鳳閣 = new MinorStar("鳳閣");
  public final static MinorStar 紅鸞 = new MinorStar("紅鸞");
  public final static MinorStar 天喜 = new MinorStar("天喜");
  public final static MinorStar 孤辰 = new MinorStar("孤辰");
  public final static MinorStar 寡宿 = new MinorStar("寡宿");
  public final static MinorStar 蜚廉 = new MinorStar("蜚廉");
  public final static MinorStar 破碎 = new MinorStar("破碎");
  public final static MinorStar 華蓋 = new MinorStar("華蓋");
  public final static MinorStar 咸池 = new MinorStar("咸池");

  public final static MinorStar 天德 = new MinorStar("天德");
  public final static MinorStar 月德 = new MinorStar("月德");

  public final static MinorStar 天才 = new MinorStar("天才");
  public final static MinorStar 天壽 = new MinorStar("天壽");
  public final static MinorStar 三台 = new MinorStar("三台");
  public final static MinorStar 八座 = new MinorStar("八座");
  public final static MinorStar 恩光 = new MinorStar("恩光");
  public final static MinorStar 天貴 = new MinorStar("天貴");

  public final static MinorStar[] values = {天官, 天福, 天廚, 天刑, 天姚, 解神, 天巫, 天月, 陰煞, 台輔, 封誥, 天空, 天哭, 天虛, 龍池, 鳳閣, 紅鸞, 天喜, 孤辰, 寡宿, 蜚廉, 破碎, 華蓋, 咸池, 天德, 月德, 天才, 天壽, 三台, 八座, 恩光, 天貴};

  public MinorStar(String nameKey) {
    super(nameKey, ZStar.class.getName());
  }

  /** 天官 : 年干 -> 地支 */
  public final static Function<Stem, Branch> fun天官 = year -> {
    switch (year) {
      case 甲: return 未;
      case 乙: return 辰;
      case 丙: return 巳;
      case 丁: return 寅;
      case 戊: return 卯;
      case 己: case 辛: return 酉;
      case 庚: return 亥;
      case 壬: return 戌;
      case 癸: return 午;
      default: throw new AssertionError(year);
    }
  };

  /** 天福 : 年干 -> 地支 */
  public final static Function<Stem , Branch> fun天福 = year -> {
    switch (year) {
      case 甲: return 酉;
      case 乙: return 申;
      case 丙: return 子;
      case 丁: return 亥;
      case 戊: return 卯;
      case 己: return 寅;
      case 庚: case 壬: return 午;
      case 辛: case 癸: return 巳;
      default: throw new AssertionError(year);
    }
  };

  /**
   * 天廚 : 年干 -> 地支
   * 安天廚訣曰：『甲丁食蛇口，乙戊辛馬方，丙從鼠口得，己食於猴房，庚食虎頭上，壬雞癸豬堂。』
   */
  public final static Function<Stem , Branch> fun天廚 = year -> {
    switch (year) {
      case 甲: case 丁: return 巳;
      case 乙: case 戊: case 辛: return 午;
      case 丙: return 子;
      case 己: return 申;
      case 庚: return 寅;
      case 壬: case 癸: return 亥;
      default: throw new AssertionError(year);
    }
  };

  /** 天刑 : 月支 -> 地支 */
  public final static Function<Branch , Branch> fun天刑 = month -> Branch.get(month.getIndex()+7);

  /** 天姚 : 月支 -> 地支 */
  public final static Function<Branch , Branch> fun天姚 = month -> month.next(11);

  /** 解神 : 月支 -> 地支 */
  public final static Function<Branch , Branch> fun解神 = month -> {
    switch (month) {
      case 寅: case 卯: return 申;
      case 辰: case 巳: return 戌;
      case 午: case 未: return 子;
      case 申: case 酉: return 寅;
      case 戌: case 亥: return 辰;
      case 子: case 丑: return 午;
      default: throw new AssertionError(month);
    }
  };

  /** 天巫 : 月支 -> 地支 */
  public final static Function<Branch , Branch> fun天巫 = month -> {
    switch (BranchTools.trilogy(month)) {
      case 火: return 巳;
      case 木: return 申;
      case 水: return 寅;
      case 金: return 亥;
      default: throw new AssertionError(month);
    }
  };

  /** 天月 : 月支 -> 地支 */
  public final static Function<Branch , Branch> fun天月 = month -> {
    switch (month) {
      case 寅: return 戌;
      case 卯: return 巳;
      case 辰: return 辰;
      case 巳: case 戌: return 寅;
      case 午: case 酉: return 未;
      case 未: return 卯;
      case 申: return 亥;
      case 亥: case 丑: return 午;
      case 子: return 戌;
      default: throw new AssertionError(month);
    }
  };

  /** 陰煞 : 月支 -> 地支 */
  public final static Function<Branch , Branch> fun陰煞 = month -> {
    switch (month) {
      case 寅: case 申: return 寅;
      case 卯: case 酉: return 子;
      case 辰: case 戌: return 戌;
      case 巳: case 亥: return 申;
      case 午: case 子: return 午;
      case 未: case 丑: return 辰;
      default: throw new AssertionError(month);
    }
  };

  /** 台輔 : 時支 -> 地支 */
  public final static Function<Branch , Branch> fun台輔 = hour -> Branch.get(hour.getIndex()+6);

  /** 封誥 : 時支 -> 地支 */
  public final static Function<Branch , Branch> fun封誥 = hour -> Branch.get(hour.getIndex()+2);

  /** 天空 : 年支 -> 地支. 注意其與 {@link UnluckyStar#fun地空} 是不同的星/演算法 */
  public final static Function<Branch , Branch> fun天空 = year -> Branch.get(year.getIndex() + 1);

  /** 天哭 : 年支 -> 地支 */
  public final static Function<Branch , Branch> fun天哭 = year -> Branch.get(6 - year.getIndex());

  /** 天虛 : 年支 -> 地支 */
  public final static Function<Branch , Branch> fun天虛 = year -> Branch.get(year.getIndex() + 6);

  /** 龍池 : 年支 -> 地支 */
  public final static Function<Branch , Branch> fun龍池 = year -> Branch.get(year.getIndex() + 4);

  /** 鳳閣 : 年支 -> 地支 */
  public final static Function<Branch , Branch> fun鳳閣 = year -> Branch.get(10 - year.getIndex());

  /** 紅鸞 : 年支 -> 地支 */
  public final static Function<Branch , Branch> fun紅鸞 = year -> Branch.get(3 - year.getIndex());

  /** 天喜 : 年支 -> 地支 */
  public final static Function<Branch , Branch> fun天喜 = year -> Branch.get(9 - year.getIndex());

  /** 孤辰 : 年支 -> 地支 */
  public final static Function<Branch , Branch> fun孤辰 = year -> {
    switch (direction(year)) {
      case 水: return 寅;
      case 木: return 巳;
      case 火: return 申;
      case 金: return 亥;
      default: throw new AssertionError(year);
    }
  };

  /** 寡宿 : 年支 -> 地支 */
  public final static Function<Branch , Branch> fun寡宿 = year -> {
    switch (direction(year)) {
      case 水: return 戌;
      case 木: return 丑;
      case 火: return 辰;
      case 金: return 未;
      default: throw new AssertionError(year);
    }
  };

  /** 蜚廉 : 年支 -> 地支 */
  public final static Function<Branch , Branch> fun蜚廉 = year -> {
    switch (year) {
      case 子: return 申;
      case 丑: return 酉;
      case 寅: return 戌;

      case 卯: return 巳;
      case 辰: return 午;
      case 巳: return 未;

      case 午: return 寅;
      case 未: return 卯;
      case 申: return 辰;

      case 酉: return 亥;
      case 戌: return 子;
      case 亥: return 丑;

      default: throw new AssertionError(year);
    }
  };

  /** 破碎 : 年支 -> 地支 */
  public final static Function<Branch , Branch> fun破碎 = year -> {
    switch (year) {
      case 子: case 午: case 卯: case 酉: return 巳;
      case 寅: case 巳: case 申: case 亥: return 酉;
      case 辰: case 戌: case 丑: case 未: return 丑;
      default: throw new AssertionError(year);
    }
  };


  /** 華蓋 : 年支 -> 地支
   * 子辰申年在辰, 丑巳酉年在丑, 寅午戍年在戍, 卯未亥年在未
   * */
  public final static Function<Branch , Branch> fun華蓋 = year -> {
    switch (trilogy(year)) {
      case 水: return 辰;
      case 金: return 丑;
      case 火: return 戌;
      case 木: return 未;
      default: throw new AssertionError(year);
    }
  };

  /** 咸池 : 年支 -> 地支
   * 子辰申年在酉, 丑巳酉年在午, 寅午戍年在卯, 卯未亥年在子
   * */
  public final static Function<Branch , Branch> fun咸池 = year -> {
    switch (trilogy(year)) {
      case 水: return 酉;
      case 金: return 午;
      case 火: return 卯;
      case 木: return 子;
      default: throw new AssertionError(year);
    }
  };

  /** 天德 : 年支 -> 地支
   * 天德星從酉上起子，順數至流年太歲上是也。
   * 出生年 子..丑..寅..卯..辰..巳..午..未..申..酉..戌..亥
   * 天德宫 酉..戌..亥..子..丑..寅..卯..辰..巳..午..未..申
   * */
  public final static Function<Branch , Branch> fun天德 = year -> Branch.get(year.getIndex()+9);

  /** 月德 : 年支 -> 地支
   *  月德星從子上起，順至流年太歲上是也。
   * 出生年 子..丑..寅..卯..辰..巳..午..未..申..酉..戌..亥
   * 月德宫 巳..午..未..申..酉..戌..亥..子..丑..寅..卯..辰
   */
  public final static Function<Branch , Branch> fun月德 = year -> Branch.get(year.getIndex()+5);

  /** 天才 (年支 , 月數 , 時支) -> 地支
   * 天才由命宮起子, 順行至本生 「年支」安之.
   * */
  public final static Function3<Branch , Integer , Branch , Branch> fun天才 = (Branch year, Integer month, Branch hour) -> ZiweiIF.getMainHouseBranch(month , hour).next(year.getIndex());

  /** 天壽 (年支 , 月數 , 時支) -> 地支
   * 天壽由身宮起子, 順行至本生 「年支」安之 */
  public final static Function3<Branch , Integer , Branch , Branch> fun天壽 = (Branch year, Integer month, Branch hour) -> ZiweiIF.getBodyHouse(month , hour).next(year.getIndex());

  /** 三台 : (月支,日數) -> 地支. 從「左輔」取初一，順行，數到本日生 */
  public final static BiFunction<Branch , Integer , Branch> fun三台 = (month , day) -> fun左輔.apply(month).next(day-1);

  /** 八座 : (月支,日數) -> 地支. 從「右弼」取初一，逆行，數到本日生 */
  public final static BiFunction<Branch , Integer , Branch> fun八座 = (month , day) -> fun右弼.apply(month).prev(day-1);

  /** 恩光 : (時支,日數) -> 地支. 從「文昌」上取初一，順行，數到本日生，再後退一步 */
  public final static BiFunction<Branch , Integer , Branch> fun恩光 = (hour , day) -> fun文昌.apply(hour).next(day-2);

  /** 天貴 : (時支,日數) -> 地支. 從「文曲」上取初一，順行，數到本日生，再後退一步
   * NOTE : 有的書寫「逆行」，跟據比對，應該是錯誤 */
  public final static BiFunction<Branch , Integer , Branch> fun天貴 = (hour , day) -> fun文曲.apply(hour).next(day-2);

}
