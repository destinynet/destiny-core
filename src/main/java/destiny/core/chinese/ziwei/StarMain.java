/**
 * Created by smallufo on 2017-04-10.
 */
package destiny.core.chinese.ziwei;

import destiny.core.chinese.Branch;
import org.jooq.lambda.function.Function5;

import static destiny.core.chinese.Branch.*;

/**
 * 14 顆主星
 * */
public final class StarMain extends ZStar {

  public final static StarMain 紫微 = new StarMain("紫微");
  public final static StarMain 天機 = new StarMain("天機");
  public final static StarMain 太陽 = new StarMain("太陽");
  public final static StarMain 武曲 = new StarMain("武曲");
  public final static StarMain 天同 = new StarMain("天同");
  public final static StarMain 廉貞 = new StarMain("廉貞");

  public final static StarMain 天府 = new StarMain("天府");
  public final static StarMain 太陰 = new StarMain("太陰");
  public final static StarMain 貪狼 = new StarMain("貪狼");
  public final static StarMain 巨門 = new StarMain("巨門");
  public final static StarMain 天相 = new StarMain("天相");
  public final static StarMain 天梁 = new StarMain("天梁");
  public final static StarMain 七殺 = new StarMain("七殺");
  public final static StarMain 破軍 = new StarMain("破軍");

  public final static StarMain[] values = {紫微 , 天機 , 太陽 , 武曲 , 天同 , 廉貞 , 天府 , 太陰 , 貪狼 , 巨門 , 天相 , 天梁 , 七殺 , 破軍};

  public StarMain(String nameKey) {
    // resource key 存放於 destiny.core.chinese.ziwei.ZStar.properties 當中
    super(nameKey , ZStar.class.getName() , nameKey+"_ABBR", Type.主星);
  }


  // （局數 , 日數 , 是否閏月 , 上個月的天數 , 紫微星實作) -> 地支
  public final static Function5<Integer, Integer, Boolean , Integer, IPurpleStarBranch , Branch> fun紫微 = (set , days , leap , prevMonthDays , iPurpleBranch) -> iPurpleBranch.getBranchOfPurpleStar(set , days , leap , prevMonthDays);
  public final static Function5<Integer, Integer, Boolean , Integer, IPurpleStarBranch , Branch> fun天機 = (set , days , leap , prevMonthDays , iPurpleBranch) -> fun紫微.apply(set , days , leap ,prevMonthDays , iPurpleBranch).prev(1);
  public final static Function5<Integer, Integer, Boolean , Integer, IPurpleStarBranch , Branch> fun太陽 = (set , days , leap , prevMonthDays , iPurpleBranch) -> fun紫微.apply(set , days , leap ,prevMonthDays , iPurpleBranch).prev(3);
  public final static Function5<Integer, Integer, Boolean , Integer, IPurpleStarBranch , Branch> fun武曲 = (set , days , leap , prevMonthDays , iPurpleBranch) -> fun紫微.apply(set , days , leap ,prevMonthDays , iPurpleBranch).prev(4);
  public final static Function5<Integer, Integer, Boolean , Integer, IPurpleStarBranch , Branch> fun天同 = (set , days , leap , prevMonthDays , iPurpleBranch) -> fun紫微.apply(set , days , leap ,prevMonthDays , iPurpleBranch).prev(5);
  public final static Function5<Integer, Integer, Boolean , Integer, IPurpleStarBranch , Branch> fun廉貞 = (set , days , leap , prevMonthDays , iPurpleBranch) -> fun紫微.apply(set , days , leap ,prevMonthDays , iPurpleBranch).prev(8);

  public final static Function5<Integer, Integer, Boolean , Integer, IPurpleStarBranch , Branch> fun天府 = (set , days , leap , prevMonthDays , iPurpleBranch) -> getOpposite(fun紫微.apply(set , days , leap , prevMonthDays , iPurpleBranch));
  public final static Function5<Integer, Integer, Boolean , Integer, IPurpleStarBranch , Branch> fun太陰 = (set , days , leap , prevMonthDays , iPurpleBranch) -> fun天府.apply(set , days , leap , prevMonthDays , iPurpleBranch).next(1);
  public final static Function5<Integer, Integer, Boolean , Integer, IPurpleStarBranch , Branch> fun貪狼 = (set , days , leap , prevMonthDays , iPurpleBranch) -> fun天府.apply(set , days , leap , prevMonthDays , iPurpleBranch).next(2);
  public final static Function5<Integer, Integer, Boolean , Integer, IPurpleStarBranch , Branch> fun巨門 = (set , days , leap , prevMonthDays , iPurpleBranch) -> fun天府.apply(set , days , leap , prevMonthDays , iPurpleBranch).next(3);
  public final static Function5<Integer, Integer, Boolean , Integer, IPurpleStarBranch , Branch> fun天相 = (set , days , leap , prevMonthDays , iPurpleBranch) -> fun天府.apply(set , days , leap , prevMonthDays , iPurpleBranch).next(4);
  public final static Function5<Integer, Integer, Boolean , Integer, IPurpleStarBranch , Branch> fun天梁 = (set , days , leap , prevMonthDays , iPurpleBranch) -> fun天府.apply(set , days , leap , prevMonthDays , iPurpleBranch).next(5);
  public final static Function5<Integer, Integer, Boolean , Integer, IPurpleStarBranch , Branch> fun七殺 = (set , days , leap , prevMonthDays , iPurpleBranch) -> fun天府.apply(set , days , leap , prevMonthDays , iPurpleBranch).next(6);
  public final static Function5<Integer, Integer, Boolean , Integer, IPurpleStarBranch , Branch> fun破軍 = (set , days , leap , prevMonthDays , iPurpleBranch) -> fun天府.apply(set , days , leap , prevMonthDays , iPurpleBranch).next(10);

  /**
   * 以「寅申」為軸，取得對宮的地支
   * @param branch
   * @return
   */
  private final static Branch getOpposite(Branch branch) {
    switch (branch) {
      case 寅: return 寅;
      case 申: return 申;
      case 卯: return 丑;
      case 丑: return 卯;
      case 辰: return 子;
      case 子: return 辰;
      case 巳: return 亥;
      case 亥: return 巳;
      case 午: return 戌;
      case 戌: return 午;
      case 未: return 酉;
      case 酉: return 未;
      default: throw new AssertionError("Error : " + branch);
    }
  }

}
