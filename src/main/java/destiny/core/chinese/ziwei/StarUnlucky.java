/**
 * Created by smallufo on 2017-04-11.
 */
package destiny.core.chinese.ziwei;

import destiny.core.chinese.Branch;
import destiny.core.chinese.BranchTools;
import destiny.core.chinese.Stem;

import java.util.function.BiFunction;
import java.util.function.Function;

import static destiny.core.chinese.Branch.*;

/**
 * 六兇星
 */
@SuppressWarnings("Duplicates")
public final class StarUnlucky extends ZStar {

  public final static StarUnlucky 擎羊 = new StarUnlucky("擎羊"); // 甲
  public final static StarUnlucky 陀羅 = new StarUnlucky("陀羅"); // 甲
  public final static StarUnlucky 火星 = new StarUnlucky("火星"); // 甲
  public final static StarUnlucky 鈴星 = new StarUnlucky("鈴星"); // 甲
  public final static StarUnlucky 地劫 = new StarUnlucky("地劫"); // 乙
  public final static StarUnlucky 地空 = new StarUnlucky("地空"); // 乙 (有時又稱天空)

  public final static StarUnlucky[] values = {擎羊 , 陀羅 , 火星 , 鈴星 , 地劫 , 地空};

  public StarUnlucky(String nameKey) {
    super(nameKey, ZStar.class.getName() , nameKey+"_ABBR");
  }

  /** 擎羊 : 年干 -> 地支 */
  public final static Function<Stem, Branch> fun擎羊 = year -> {
    switch (year) {
      case 甲: return 卯;
      case 乙: return 辰;
      case 丙: case 戊: return 午;
      case 丁: case 己: return 未;
      case 庚: return 酉;
      case 辛: return 戌;
      case 壬: return 子;
      case 癸: return 丑;
      default: throw new AssertionError(year);
    }
  };

  /** 陀羅 : 年干 -> 地支 */
  public final static Function<Stem , Branch> fun陀羅 = year -> {
    switch (year) {
      case 甲: return 丑;
      case 乙: return 寅;
      case 丙: case 戊: return 辰;
      case 丁: case 己: return 巳;
      case 庚: return 未;
      case 辛: return 申;
      case 壬: return 戌;
      case 癸: return 亥;
      default: throw new AssertionError(year);
    }
  };

  /**
   * 參考資料
   * https://destiny.to/ubbthreads/ubbthreads.php/topics/14679
   *
   *
   * 全書和全集裡對火鈴的排法有所不同：
   * 1. 全書是根據生年年支來安火鈴：
   *   寅午戌人丑卯方  子申辰人寅戌揚
   *   巳酉丑人卯戌位  亥卯未人酉戌房
   *
   */
  /** 火星 (全書): 年支 -> 地支 */
  public final static Function<Branch , Branch> fun火星_全書 = year -> {
    switch (BranchTools.trilogy(year)) {
      case 火: return 丑; // 寅午戌人[丑]卯方
      case 水: return 寅; // 子申辰人[寅]戌揚
      case 金: return 卯; // 巳酉丑人[卯]戌位
      case 木: return 酉; // 亥卯未人[酉]戌房
      default: throw new AssertionError(year);
    }
  };

  /** 鈴星 (全書): 年支 -> 地支 */
  public final static Function<Branch , Branch> fun鈴星_全書 = year -> {
    switch (BranchTools.trilogy(year)) {
      case 火: return 卯; // 寅午戌人丑[卯]方
      case 水: return 戌; // 子申辰人寅[戌]揚
      case 金: return 戌; // 巳酉丑人卯[戌]位
      case 木: return 戌; // 亥卯未人酉[戌]房
      default: throw new AssertionError(year);
    }
  };

  /**
   * 2. 全集則是根據生年年支及生時來安火鈴
   *   申子辰人寅火戌鈴  寅午戌人丑火卯鈴
   *   亥卯未人酉火戌鈴  巳酉丑人戌火卯鈴
   *   接著有一段說明
   *      凡命俱以生年十二支為主假如申子辰子時生人則寅宮起子時順數至本人生時安火星戌宮起
   *      子時順數至本人生時安鈴星假如甲申年丑時生人則卯宮安火亥宮安鈴餘仿此
   */

  /** 火星 (全集): (年支、時支) -> 地支 (子由使用)*/
  public final static BiFunction<Branch , Branch , Branch> fun火星_全集 = (year , hour) -> {
    switch (BranchTools.trilogy(year)) {
      case 火: return Branch.get(hour.getIndex()+1);
      case 水: return Branch.get(hour.getIndex()+2);
      case 金: return Branch.get(hour.getIndex()+3);
      case 木: return Branch.get(hour.getIndex()+9);
      default: throw new AssertionError("年支 = " + year + " , 時支 = " + hour);
    }
  };

  /** 鈴星 (全集) : (年支、時支) -> 地支 (子由使用) */
  public final static BiFunction<Branch , Branch , Branch> fun鈴星_全集 = (year , hour ) -> {
    switch (BranchTools.trilogy(year)) {
      case 火: return Branch.get(hour.getIndex()+3);
      case 水:
      case 金:
      case 木:
        return Branch.get(hour.getIndex() + 10);
      default: throw new AssertionError("年支 = " + year + " , 時支 = " + hour);
    }
  };

  /** 地劫 : 時支 -> 地支 */
  public final static Function<Branch , Branch> fun地劫 = hour -> Branch.get(hour.getIndex()-1);

  /** 地空 : 時支 -> 地支 */
  public final static Function<Branch , Branch> fun地空 = hour -> Branch.get(11-hour.getIndex());
}
