/**
 * Created by smallufo on 2017-04-12.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Gender;
import destiny.core.chinese.Branch;
import destiny.core.chinese.BranchTools;
import destiny.core.chinese.Stem;
import org.jooq.lambda.function.Function3;

import java.util.function.BiFunction;
import java.util.function.Function;

import static destiny.core.Gender.女;
import static destiny.core.Gender.男;
import static destiny.core.chinese.Branch.*;
import static destiny.core.chinese.BranchTools.direction;
import static destiny.core.chinese.BranchTools.trilogy;
import static destiny.core.chinese.ziwei.StarLucky.*;

/**
 * 乙級星有總共有34顆
 */
@SuppressWarnings("Duplicates")
public class StarMinor extends ZStar {

  public final static StarMinor 天官 = new StarMinor("天官"); // 吉
  public final static StarMinor 天福 = new StarMinor("天福"); // 吉
  public final static StarMinor 天廚 = new StarMinor("天廚");
  public final static StarMinor 天刑 = new StarMinor("天刑"); //   兇
  public final static StarMinor 天姚 = new StarMinor("天姚"); //   兇
  public final static StarMinor 解神 = new StarMinor("解神"); // 吉
  public final static StarMinor 天巫 = new StarMinor("天巫"); // 吉
  public final static StarMinor 天月 = new StarMinor("天月"); //   兇
  public final static StarMinor 陰煞 = new StarMinor("陰煞"); //   兇
  public final static StarMinor 台輔 = new StarMinor("台輔"); // 吉
  public final static StarMinor 封誥 = new StarMinor("封誥"); // 吉
  public final static StarMinor 天空 = new StarMinor("天空"); //   兇
  public final static StarMinor 天哭 = new StarMinor("天哭"); //   兇
  public final static StarMinor 天虛 = new StarMinor("天虛"); //   兇
  public final static StarMinor 龍池 = new StarMinor("龍池"); // 吉
  public final static StarMinor 鳳閣 = new StarMinor("鳳閣"); // 吉
  public final static StarMinor 紅鸞 = new StarMinor("紅鸞"); // 吉
  public final static StarMinor 天喜 = new StarMinor("天喜"); // 吉
  public final static StarMinor 孤辰 = new StarMinor("孤辰"); //   兇
  public final static StarMinor 寡宿 = new StarMinor("寡宿"); //   兇
  public final static StarMinor 蜚廉 = new StarMinor("蜚廉");
  public final static StarMinor 破碎 = new StarMinor("破碎");
  public final static StarMinor 華蓋 = new StarMinor("華蓋"); //   兇
  public final static StarMinor 咸池 = new StarMinor("咸池"); //   兇
  public final static StarMinor 天德 = new StarMinor("天德"); //   兇?
  public final static StarMinor 月德 = new StarMinor("月德"); //   兇?
  public final static StarMinor 天才 = new StarMinor("天才"); // 吉
  public final static StarMinor 天壽 = new StarMinor("天壽"); // 吉
  public final static StarMinor 三台 = new StarMinor("三台"); // 吉
  public final static StarMinor 八座 = new StarMinor("八座"); // 吉
  public final static StarMinor 恩光 = new StarMinor("恩光"); // 吉
  public final static StarMinor 天貴 = new StarMinor("天貴"); // 吉

  public final static StarMinor 天使 = new StarMinor("天使"); //   兇 , 天使屬陰水 , 主災病
  public final static StarMinor 天傷 = new StarMinor("天傷"); //   兇 , 天傷屬陽水 , 主虛耗

  // TODO
  public final static StarMinor 截空 = new StarMinor("截空"); //   兇

  public final static StarMinor[] values = {天官, 天福, 天廚, 天刑, 天姚, 解神, 天巫, 天月, 陰煞, 台輔, 封誥, 天空, 天哭, 天虛, 龍池, 鳳閣, 紅鸞, 天喜, 孤辰, 寡宿, 蜚廉, 破碎, 華蓋, 咸池, 天德, 月德, 天才, 天壽, 三台, 八座, 恩光, 天貴, 天使, 天傷};

  public StarMinor(String nameKey) {
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

  /** 天空 : 年支 -> 地支. 注意其與 {@link StarUnlucky#fun地空} 是不同的星/演算法 */
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
  public final static Function3<Branch , Integer , Branch , Branch> fun天壽 = (Branch year, Integer month, Branch hour) -> ZiweiIF.getBodyHouseBranch(month , hour).next(year.getIndex());

  /** 三台 : (月支,日數) -> 地支. 從「左輔」取初一，順行，數到本日生 */
  public final static BiFunction<Branch , Integer , Branch> fun三台 = (month , day) -> fun左輔.apply(month).next(day-1);

  /** 八座 : (月支,日數) -> 地支. 從「右弼」取初一，逆行，數到本日生 */
  public final static BiFunction<Branch , Integer , Branch> fun八座 = (month , day) -> fun右弼.apply(month).prev(day-1);

  /** 恩光 : (日數,時支) -> 地支. 從「文昌」上取初一，順行，數到本日生，再後退一步 */
  public final static BiFunction<Integer , Branch , Branch> fun恩光 = (day , hour) -> fun文昌.apply(hour).next(day-2);

  /** 天貴 : (日數,時支) -> 地支. 從「文曲」上取初一，順行，數到本日生，再後退一步
   * NOTE : 有的書寫「逆行」，跟據比對，應該是錯誤 */
  public final static BiFunction<Integer , Branch , Branch> fun天貴 = (day , hour) -> fun文曲.apply(hour).next(day-2);

  /** 天傷 : 兩種算法，第 1 種 : 固定於交友宮 (亦即：遷移宮地支-1) */
  public final static Function<Branch , Branch> fun天傷_fixed交友 = (遷移宮地支) -> 遷移宮地支.prev(1);

  /** 天使 : 兩種算法，第 1 種 : 固定於疾厄宮（亦即：遷移宮地支+1）*/
  public final static Function<Branch , Branch> fun天使_fixed疾厄 = (遷移宮地支) -> 遷移宮地支.next(1);

  /** 天傷 : 兩種算法，第 2 種 : 陽男陰女順行，安天傷於交友宮 (亦即：遷移宮地支-1) */
  public final static Function3<Branch , Stem , Gender,  Branch> fun天傷_陽順陰逆 = (遷移宮地支 , 年干 , gender) -> {
    if (年干.getBooleanValue() && gender == 男 || !年干.getBooleanValue() && gender == 女) {
      return 遷移宮地支.prev(1);
    } else {
      return 遷移宮地支.next(1);
    }
  };

  /** 天使 : 兩種算法，第 2 種 : 陽男陰女順行，安天使於疾厄宮 (亦即：遷移宮地支+1) */
  public final static Function3<Branch , Stem , Gender , Branch> fun天使_陽順陰逆 = (遷移宮地支 , 年干 , gender) -> {
    if (年干.getBooleanValue() && gender == 男 || !年干.getBooleanValue() && gender == 女) {
      return 遷移宮地支.next(1);
    } else {
      return 遷移宮地支.prev(1);
    }
  };
}
