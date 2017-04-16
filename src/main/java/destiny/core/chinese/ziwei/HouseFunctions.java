/**
 * Created by smallufo on 2017-04-13.
 */
package destiny.core.chinese.ziwei;

import com.google.common.collect.ImmutableSet;
import destiny.core.Gender;
import destiny.core.chinese.Branch;
import destiny.core.chinese.Stem;
import destiny.core.chinese.StemBranch;
import destiny.core.chinese.TianyiIF;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static destiny.core.chinese.ziwei.StarDoctor.*;
import static destiny.core.chinese.ziwei.StarLucky.*;
import static destiny.core.chinese.ziwei.StarMain.*;
import static destiny.core.chinese.ziwei.StarMinor.*;
import static destiny.core.chinese.ziwei.StarUnlucky.*;
import static destiny.core.chinese.ziwei.IZiwei.*;

@SuppressWarnings("Duplicates")
public class HouseFunctions {

  public final static IHouse house紫微 = new HouseMainStarImpl(紫微) {
    @Override
    public Branch getBranch(Tuple2<Integer, Integer> t) {
      return fun紫微.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house天機 = new HouseMainStarImpl(天機) {
    @Override
    public Branch getBranch(Tuple2<Integer, Integer> t) {
      return fun天機.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house太陽 = new HouseMainStarImpl(太陽) {
    @Override
    public Branch getBranch(Tuple2<Integer, Integer> t) {
      return fun太陽.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house武曲 = new HouseMainStarImpl(武曲) {
    @Override
    public Branch getBranch(Tuple2<Integer, Integer> t) {
      return fun武曲.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house天同 = new HouseMainStarImpl(天同) {
    @Override
    public Branch getBranch(Tuple2<Integer, Integer> t) {
      return fun天同.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house廉貞 = new HouseMainStarImpl(廉貞) {
    @Override
    public Branch getBranch(Tuple2<Integer, Integer> t) {
      return fun廉貞.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house天府 = new HouseMainStarImpl(天府) {
    @Override
    public Branch getBranch(Tuple2<Integer, Integer> t) {
      return fun天府.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house太陰 = new HouseMainStarImpl(太陰) {
    @Override
    public Branch getBranch(Tuple2<Integer, Integer> t) {
      return fun太陰.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house貪狼 = new HouseMainStarImpl(貪狼) {
    @Override
    public Branch getBranch(Tuple2<Integer, Integer> t) {
      return fun貪狼.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house巨門 = new HouseMainStarImpl(巨門) {
    @Override
    public Branch getBranch(Tuple2<Integer, Integer> t) {
      return fun巨門.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house天相 = new HouseMainStarImpl(天相) {
    @Override
    public Branch getBranch(Tuple2<Integer, Integer> t) {
      return fun天相.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house天梁 = new HouseMainStarImpl(天梁) {
    @Override
    public Branch getBranch(Tuple2<Integer, Integer> t) {
      return fun天梁.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house七殺 = new HouseMainStarImpl(七殺) {
    @Override
    public Branch getBranch(Tuple2<Integer, Integer> t) {
      return fun七殺.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house破軍 = new HouseMainStarImpl(破軍) {
    @Override
    public Branch getBranch(Tuple2<Integer, Integer> t) {
      return fun破軍.apply(t.v1() , t.v2());
    }
  };

  // =======↑↑↑======= 以上 14 顆主星 =======↑↑↑=======

  // =======↓↓↓======= 以下  8 顆吉星 =======↓↓↓=======
  
  public final static IHouse house文昌 = new HouseHourBranchImpl(文昌) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun文昌.apply(branch);
    }
  };

  public final static IHouse house文曲 = new HouseHourBranchImpl(文曲) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun文曲.apply(branch);
    }
  };

  public final static IHouse house左輔 = new HouseMonthBranchImpl(左輔) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun左輔.apply(branch);
    }
  };
  
  public final static IHouse house右弼 = new HouseMonthBranchImpl(右弼) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun右弼.apply(branch);
    }
  };
  
  public final static IHouse house天魁 = new HouseYearStemTianyiImpl(天魁) {
    @Override
    public Branch getBranch(Tuple2<Stem, TianyiIF> tuple) {
      return fun天魁.apply(tuple.v1() , tuple.v2());
    }
  };
  
  public final static IHouse house天鉞 = new HouseYearStemTianyiImpl(天鉞) {
    @Override
    public Branch getBranch(Tuple2<Stem, TianyiIF> tuple) {
      return fun天鉞.apply(tuple.v1() , tuple.v2());
    }
  };
  
  public final static IHouse house祿存 = new HouseYearStemImpl(祿存) {
    @Override
    public Branch getBranch(Stem stem) {
      return fun祿存.apply(stem);
    }
  };
  
  public final static IHouse house天馬 = new HouseYearBranchMonthBranchImpl(天馬) {
    @Override
    public Branch getBranch(StemBranch year, Branch monthBranch, int monthNum, int days, Branch hour, int set, Gender gender, Settings settings) {
      switch (settings.getHorse()) {
        case 年馬: return fun天馬.apply(year.getBranch());
        case 月馬: return fun天馬.apply(monthBranch);
        default: throw new AssertionError("error");
      }
    }
  };
  
  // =======↑↑↑======= 以上  8 顆吉星 =======↑↑↑=======
  
  // =======↓↓↓======= 以下  6 顆兇星 =======↓↓↓=======

  public final static IHouse house擎羊 = new HouseYearStemImpl(擎羊) {
    @Override
    public Branch getBranch(Stem stem) {
      return fun擎羊.apply(stem);
    }
  };

  public final static IHouse house陀羅 = new HouseYearStemImpl(陀羅) {
    @Override
    public Branch getBranch(Stem stem) {
      return fun陀羅.apply(stem);
    }
  };

  public final static IHouse house火星 = new HouseYearBranchHourBranchImpl(火星) {
    @Override
    public Branch getBranch(Tuple2<Branch, Branch> objects) {
      throw new RuntimeException("error : " + objects);
    }

    @Override
    public Branch getBranch(StemBranch year, Branch monthBranch, int monthNum, int days, Branch hour, int set, Gender gender, Settings settings) {
      switch (settings.getFireBell()) {
        case 全書: return fun火星_全書.apply(year.getBranch());
        case 全集: return fun火星_全集.apply(year.getBranch() , hour);
        default: throw new AssertionError("error");
      }
    }
  };

  public final static IHouse house鈴星 = new HouseYearBranchHourBranchImpl(鈴星) {
    @Override
    public Branch getBranch(Tuple2<Branch, Branch> objects) {
      throw new RuntimeException("error : " + objects);
    }

    @Override
    public Branch getBranch(StemBranch year, Branch monthBranch, int monthNum, int days, Branch hour, int set, Gender gender, Settings settings) {
      switch (settings.getFireBell()) {
        case 全書: return fun鈴星_全書.apply(year.getBranch());
        case 全集: return fun鈴星_全集.apply(year.getBranch() , hour);
        default: throw new AssertionError("error");
      }
    }
  };

  public final static IHouse house地劫 = new HouseHourBranchImpl(地劫) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun地劫.apply(branch);
    }
  };

  public final static IHouse house地空 = new HouseHourBranchImpl(地空) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun地空.apply(branch);
    }
  };


  // =======↑↑↑======= 以上  6 顆兇星 =======↑↑↑=======



  // =======↓↓↓======= 以下      雜曜 =======↓↓↓=======
  public final static IHouse house天官 = new HouseYearStemImpl(天官) {
    @Override
    public Branch getBranch(Stem stem) {
      return fun天官.apply(stem);
    }
  };

  public final static IHouse house天福 = new HouseYearStemImpl(天福) {
    @Override
    public Branch getBranch(Stem stem) {
      return fun天福.apply(stem);
    }
  };

  public final static IHouse house天廚 = new HouseYearStemImpl(天廚) {
    @Override
    public Branch getBranch(Stem stem) {
      return fun天廚.apply(stem);
    }
  };

  public final static IHouse house天刑 = new HouseMonthBranchImpl(天刑) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun天刑.apply(branch);
    }
  };

  public final static IHouse house天姚 = new HouseMonthBranchImpl(天姚) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun天姚.apply(branch);
    }
  };

  public final static IHouse house解神 = new HouseMonthBranchImpl(解神) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun解神.apply(branch);
    }
  };

  public final static IHouse house天巫 = new HouseMonthBranchImpl(天巫) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun天巫.apply(branch);
    }
  };

  public final static IHouse house天月 = new HouseMonthBranchImpl(天月) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun天月.apply(branch);
    }
  };

  public final static IHouse house陰煞 = new HouseMonthBranchImpl(陰煞) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun陰煞.apply(branch);
    }
  };

  public final static IHouse house台輔 = new HouseHourBranchImpl(台輔) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun台輔.apply(branch);
    }
  };

  public final static IHouse house封誥 = new HouseHourBranchImpl(封誥) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun封誥.apply(branch);
    }
  };

  public final static IHouse house天空 = new HouseYearBranchImpl(天空) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun天空.apply(branch);
    }
  };

  public final static IHouse house天哭 = new HouseYearBranchImpl(天哭) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun天哭.apply(branch);
    }
  };

  public final static IHouse house天虛 = new HouseYearBranchImpl(天虛) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun天虛.apply(branch);
    }
  };

  public final static IHouse house龍池 = new HouseYearBranchImpl(龍池) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun龍池.apply(branch);
    }
  };

  public final static IHouse house鳳閣 = new HouseYearBranchImpl(鳳閣) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun鳳閣.apply(branch);
    }
  };

  public final static IHouse house紅鸞 = new HouseYearBranchImpl(紅鸞) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun紅鸞.apply(branch);
    }
  };

  public final static IHouse house天喜 = new HouseYearBranchImpl(天喜) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun天喜.apply(branch);
    }
  };

  public final static IHouse house孤辰 = new HouseYearBranchImpl(孤辰) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun孤辰.apply(branch);
    }
  };

  public final static IHouse house寡宿 = new HouseYearBranchImpl(寡宿) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun寡宿.apply(branch);
    }
  };

  public final static IHouse house蜚廉 = new HouseYearBranchImpl(蜚廉) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun蜚廉.apply(branch);
    }
  };

  public final static IHouse house破碎 = new HouseYearBranchImpl(破碎) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun破碎.apply(branch);
    }
  };

  public final static IHouse house華蓋 = new HouseYearBranchImpl(華蓋) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun華蓋.apply(branch);
    }
  };

  public final static IHouse house咸池 = new HouseYearBranchImpl(咸池) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun咸池.apply(branch);
    }
  };

  public final static IHouse house天德 = new HouseYearBranchImpl(天德) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun天德.apply(branch);
    }
  };

  public final static IHouse house月德 = new HouseYearBranchImpl(月德) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun月德.apply(branch);
    }
  };

  public final static IHouse house天才 = new HouseYearBranchMonthNumHourBranchImpl(天才) {
    @Override
    public Branch getBranch(Tuple3<Branch, Integer, Branch> t) {
      return fun天才.apply(t.v1() , t.v2() , t.v3());
    }
  };

  public final static IHouse house天壽 = new HouseYearBranchMonthNumHourBranchImpl(天壽) {
    @Override
    public Branch getBranch(Tuple3<Branch, Integer, Branch> t) {
      return fun天壽.apply(t.v1() , t.v2() , t.v3());
    }
  };

  public final static IHouse house三台 = new HouseMonthBranchDayNumImpl(三台) {
    @Override
    public Branch getBranch(Tuple2<Branch, Integer> t) {
      return fun三台.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house八座 = new HouseMonthBranchDayNumImpl(八座) {
    @Override
    public Branch getBranch(Tuple2<Branch, Integer> t) {
      return fun八座.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house恩光 = new HouseDayNumHourBranchImpl(恩光) {
    @Override
    public Branch getBranch(Tuple2<Integer, Branch> t) {
      return fun恩光.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house天貴 = new HouseDayNumHourBranchImpl(天貴) {
    @Override
    public Branch getBranch(Tuple2<Integer, Branch> t) {
      return fun天貴.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house天傷 = new HouseHouseDepYearStemGenderImpl(天傷) {
    @Override
    public Branch getBranch(StemBranch year, Branch monthBranch, int monthNum, int days, Branch hour, int set, Gender gender, Settings settings) {
      // 太乙派，沒有遷移宮
      Branch 遷移宮地支 = IZiwei.getHouseBranch(monthNum, hour, House.遷移, new HouseSeqDefaultImpl());
      switch (settings.getHurtAngel()) {
        case FIXED  : return fun天傷_fixed交友.apply(遷移宮地支);
        case YINYANG: return fun天傷_陽順陰逆.apply(遷移宮地支 , year.getStem() , gender);
        default:
          throw new AssertionError("error");
      }
    }
  };

  public final static IHouse house天使 = new HouseHouseDepYearStemGenderImpl(天使) {
    @Override
    public Branch getBranch(StemBranch year, Branch monthBranch, int monthNum, int days, Branch hour, int set, Gender gender, Settings settings) {
      // 太乙派，沒有遷移宮
      Branch 遷移宮地支 = IZiwei.getHouseBranch(monthNum, hour, House.遷移, new HouseSeqDefaultImpl());
      switch (settings.getHurtAngel()) {
        case FIXED  : return fun天使_fixed疾厄.apply(遷移宮地支);
        case YINYANG: return fun天使_陽順陰逆.apply(遷移宮地支 , year.getStem() , gender);
        default:
          throw new AssertionError("error");
      }
    }
  };

  // =======↑↑↑======= 以上      雜曜 =======↑↑↑=======

  // =======↓↓↓======= 以下 博士12神煞 =======↓↓↓=======

  public final static IHouse house博士 = new HouseYearStemGenderImpl(博士) {
    @Override
    public Branch getBranch(Tuple2<Stem, Gender> t) {
      return fun博士.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house力士 = new HouseYearStemGenderImpl(力士) {
    @Override
    public Branch getBranch(Tuple2<Stem, Gender> t) {
      return fun力士.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house青龍 = new HouseYearStemGenderImpl(青龍) {
    @Override
    public Branch getBranch(Tuple2<Stem, Gender> t) {
      return fun青龍.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house小耗 = new HouseYearStemGenderImpl(小耗) {
    @Override
    public Branch getBranch(Tuple2<Stem, Gender> t) {
      return fun小耗.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house將軍 = new HouseYearStemGenderImpl(將軍) {
    @Override
    public Branch getBranch(Tuple2<Stem, Gender> t) {
      return fun將軍.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house奏書 = new HouseYearStemGenderImpl(奏書) {
    @Override
    public Branch getBranch(Tuple2<Stem, Gender> t) {
      return fun奏書.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house飛廉 = new HouseYearStemGenderImpl(飛廉) {
    @Override
    public Branch getBranch(Tuple2<Stem, Gender> t) {
      return fun飛廉.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house喜神 = new HouseYearStemGenderImpl(喜神) {
    @Override
    public Branch getBranch(Tuple2<Stem, Gender> t) {
      return fun喜神.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house病符 = new HouseYearStemGenderImpl(病符) {
    @Override
    public Branch getBranch(Tuple2<Stem, Gender> t) {
      return fun病符.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house大耗 = new HouseYearStemGenderImpl(大耗) {
    @Override
    public Branch getBranch(Tuple2<Stem, Gender> t) {
      return fun大耗.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house伏兵 = new HouseYearStemGenderImpl(伏兵) {
    @Override
    public Branch getBranch(Tuple2<Stem, Gender> t) {
      return fun伏兵.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house官府 = new HouseYearStemGenderImpl(官府) {
    @Override
    public Branch getBranch(Tuple2<Stem, Gender> t) {
      return fun官府.apply(t.v1() , t.v2());
    }
  };

  // =======↑↑↑======= 以上 博士12神煞 =======↑↑↑=======

  public final static Set<IHouse> set = new ImmutableSet.Builder<IHouse>()
    // 14主星
    .add(house紫微, house天機, house太陽, house武曲, house天同, house廉貞, house天府, house太陰, house貪狼, house巨門, house天相, house天梁, house七殺, house破軍)

    // 八吉星
    .add(house文昌, house文曲, house左輔, house右弼, house天魁, house天鉞, house祿存, house天馬)

    // 六兇星
    .add(house擎羊, house陀羅, house火星, house鈴星, house地劫, house地空)

    // 雜曜
    .add(house天官, house天福, house天廚, house天刑, house天姚, house解神, house天巫, house天月)
    .add(house陰煞, house台輔, house封誥, house天空, house天哭, house天虛, house龍池, house鳳閣)
    .add(house紅鸞, house天喜, house孤辰, house寡宿, house蜚廉, house破碎, house華蓋, house咸池)
    .add(house天德, house月德, house天才, house天壽, house三台, house八座, house恩光, house天貴)
    .add(house天傷, house天使)

    // 博士12神煞
    .add(house博士, house力士, house青龍, house小耗, house將軍, house奏書,
         house飛廉, house喜神, house病符, house大耗, house伏兵, house官府)
    .build();

  public final static Map<ZStar , IHouse> map = set.stream()
    .collect(Collectors.toMap(IHouse::getStar, iHouse -> iHouse));

}
