/**
 * Created by smallufo on 2017-04-13.
 */
package destiny.core.chinese.ziwei;

import com.google.common.collect.ImmutableSet;
import destiny.core.chinese.Branch;
import destiny.core.chinese.Stem;
import destiny.core.chinese.StemBranch;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static destiny.core.chinese.ziwei.LuckyStar.*;
import static destiny.core.chinese.ziwei.MainStar.*;
import static destiny.core.chinese.ziwei.MinorStar.*;
import static destiny.core.chinese.ziwei.UnluckyStar.*;
import static destiny.core.chinese.ziwei.ZiweiIF.*;

@SuppressWarnings("Duplicates")
public class HouseFunctions {

  public final static IHouse house紫微 = new IHouseMainStarImpl(紫微) {
    @Override
    public Branch getBranch(Tuple2<Integer, Integer> t) {
      return fun紫微.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house天機 = new IHouseMainStarImpl(天機) {
    @Override
    public Branch getBranch(Tuple2<Integer, Integer> t) {
      return fun天機.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house太陽 = new IHouseMainStarImpl(太陽) {
    @Override
    public Branch getBranch(Tuple2<Integer, Integer> t) {
      return fun太陽.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house武曲 = new IHouseMainStarImpl(武曲) {
    @Override
    public Branch getBranch(Tuple2<Integer, Integer> t) {
      return fun武曲.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house天同 = new IHouseMainStarImpl(天同) {
    @Override
    public Branch getBranch(Tuple2<Integer, Integer> t) {
      return fun天同.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house廉貞 = new IHouseMainStarImpl(廉貞) {
    @Override
    public Branch getBranch(Tuple2<Integer, Integer> t) {
      return fun廉貞.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house天府 = new IHouseMainStarImpl(天府) {
    @Override
    public Branch getBranch(Tuple2<Integer, Integer> t) {
      return fun天府.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house太陰 = new IHouseMainStarImpl(太陰) {
    @Override
    public Branch getBranch(Tuple2<Integer, Integer> t) {
      return fun太陰.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house貪狼 = new IHouseMainStarImpl(貪狼) {
    @Override
    public Branch getBranch(Tuple2<Integer, Integer> t) {
      return fun貪狼.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house巨門 = new IHouseMainStarImpl(巨門) {
    @Override
    public Branch getBranch(Tuple2<Integer, Integer> t) {
      return fun巨門.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house天相 = new IHouseMainStarImpl(天相) {
    @Override
    public Branch getBranch(Tuple2<Integer, Integer> t) {
      return fun天相.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house天梁 = new IHouseMainStarImpl(天梁) {
    @Override
    public Branch getBranch(Tuple2<Integer, Integer> t) {
      return fun天梁.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house七殺 = new IHouseMainStarImpl(七殺) {
    @Override
    public Branch getBranch(Tuple2<Integer, Integer> t) {
      return fun七殺.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house破軍 = new IHouseMainStarImpl(破軍) {
    @Override
    public Branch getBranch(Tuple2<Integer, Integer> t) {
      return fun破軍.apply(t.v1() , t.v2());
    }
  };

  // =======↑↑↑======= 以上 14 顆主星 =======↑↑↑=======

  // =======↓↓↓======= 以下  8 顆吉星 =======↓↓↓=======
  
  public final static IHouse house文昌 = new IHouseHourBranchImpl(文昌) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun文昌.apply(branch);
    }
  };

  public final static IHouse house文曲 = new IHouseHourBranchImpl(文曲) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun文曲.apply(branch);
    }
  };

  public final static IHouse house左輔 = new IHouseHourBranchImpl(左輔) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun左輔.apply(branch);
    }
  };
  
  public final static IHouse house右弼 = new IHouseHourBranchImpl(右弼) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun右弼.apply(branch);
    }
  };
  
  public final static IHouse house天魁 = new IHouseYearStemImpl(天魁) {
    @Override
    public Branch getBranch(Stem stem) {
      return fun天魁.apply(stem);
    }
  };
  
  public final static IHouse house天鉞 = new IHouseYearStemImpl(天鉞) {
    @Override
    public Branch getBranch(Stem stem) {
      return fun天鉞.apply(stem);
    }
  };
  
  public final static IHouse house祿存 = new IHouseYearStemImpl(祿存) {
    @Override
    public Branch getBranch(Stem stem) {
      return fun祿存.apply(stem);
    }
  };
  
  public final static IHouse house天馬 = new IHouseMonthBranchImpl(天馬) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun天馬.apply(branch);
    }
  };
  
  // =======↑↑↑======= 以上  8 顆吉星 =======↑↑↑=======
  
  // =======↓↓↓======= 以下  6 顆兇星 =======↓↓↓=======

  public final static IHouse house擎羊 = new IHouseYearStemImpl(擎羊) {
    @Override
    public Branch getBranch(Stem stem) {
      return fun擎羊.apply(stem);
    }
  };

  public final static IHouse house陀羅 = new IHouseYearStemImpl(陀羅) {
    @Override
    public Branch getBranch(Stem stem) {
      return fun陀羅.apply(stem);
    }
  };

  public final static IHouse house火星 = new IHouseYearBranchHourBranchImpl(火星) {
    @Override
    public Branch getBranch(Tuple2<Branch, Branch> objects) {
      throw new RuntimeException("error : " + objects);
    }

    @Override
    public Branch getBranch(StemBranch year, Branch monthBranch, int monthNum, int days, Branch hour, int set, Settings settings) {
      switch (settings.getFireBell()) {
        case 全書: return fun火星_全書.apply(year.getBranch());
        case 全集: return fun火星_全集.apply(year.getBranch() , hour);
        default: throw new AssertionError("error");
      }
    }
  };

  public final static IHouse house鈴星 = new IHouseYearBranchHourBranchImpl(鈴星) {
    @Override
    public Branch getBranch(Tuple2<Branch, Branch> objects) {
      throw new RuntimeException("error : " + objects);
    }

    @Override
    public Branch getBranch(StemBranch year, Branch monthBranch, int monthNum, int days, Branch hour, int set, Settings settings) {
      switch (settings.getFireBell()) {
        case 全書: return fun鈴星_全書.apply(year.getBranch());
        case 全集: return fun鈴星_全集.apply(year.getBranch() , hour);
        default: throw new AssertionError("error");
      }
    }
  };

  public final static IHouse house地劫 = new IHouseHourBranchImpl(地劫) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun地劫.apply(branch);
    }
  };

  public final static IHouse house地空 = new IHouseHourBranchImpl(地空) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun地空.apply(branch);
    }
  };


  // =======↑↑↑======= 以上  6 顆兇星 =======↑↑↑=======



  // =======↓↓↓======= 以下      雜曜 =======↓↓↓=======
  public final static IHouse house天官 = new IHouseYearStemImpl(天官) {
    @Override
    public Branch getBranch(Stem stem) {
      return fun天官.apply(stem);
    }
  };

  public final static IHouse house天福 = new IHouseYearStemImpl(天福) {
    @Override
    public Branch getBranch(Stem stem) {
      return fun天福.apply(stem);
    }
  };

  public final static IHouse house天廚 = new IHouseYearStemImpl(天廚) {
    @Override
    public Branch getBranch(Stem stem) {
      return fun天廚.apply(stem);
    }
  };

  public final static IHouse house天刑 = new IHouseMonthBranchImpl(天刑) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun天刑.apply(branch);
    }
  };

  public final static IHouse house天姚 = new IHouseMonthBranchImpl(天姚) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun天姚.apply(branch);
    }
  };

  public final static IHouse house解神 = new IHouseMonthBranchImpl(解神) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun解神.apply(branch);
    }
  };

  public final static IHouse house天巫 = new IHouseMonthBranchImpl(天巫) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun天巫.apply(branch);
    }
  };

  public final static IHouse house天月 = new IHouseMonthBranchImpl(天月) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun天月.apply(branch);
    }
  };

  public final static IHouse house陰煞 = new IHouseMonthBranchImpl(陰煞) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun陰煞.apply(branch);
    }
  };

  public final static IHouse house台輔 = new IHouseHourBranchImpl(台輔) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun台輔.apply(branch);
    }
  };

  public final static IHouse house封誥 = new IHouseHourBranchImpl(封誥) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun封誥.apply(branch);
    }
  };

  public final static IHouse house天空 = new IHouseYearBranchImpl(天空) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun天空.apply(branch);
    }
  };

  public final static IHouse house天哭 = new IHouseYearBranchImpl(天哭) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun天哭.apply(branch);
    }
  };

  public final static IHouse house天虛 = new IHouseYearBranchImpl(天虛) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun天虛.apply(branch);
    }
  };

  public final static IHouse house龍池 = new IHouseYearBranchImpl(龍池) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun龍池.apply(branch);
    }
  };

  public final static IHouse house鳳閣 = new IHouseYearBranchImpl(鳳閣) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun鳳閣.apply(branch);
    }
  };

  public final static IHouse house紅鸞 = new IHouseYearBranchImpl(紅鸞) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun紅鸞.apply(branch);
    }
  };

  public final static IHouse house天喜 = new IHouseYearBranchImpl(天喜) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun天喜.apply(branch);
    }
  };

  public final static IHouse house孤辰 = new IHouseYearBranchImpl(孤辰) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun孤辰.apply(branch);
    }
  };

  public final static IHouse house寡宿 = new IHouseYearBranchImpl(寡宿) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun寡宿.apply(branch);
    }
  };

  public final static IHouse house蜚廉 = new IHouseYearBranchImpl(蜚廉) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun蜚廉.apply(branch);
    }
  };

  public final static IHouse house破碎 = new IHouseYearBranchImpl(破碎) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun破碎.apply(branch);
    }
  };

  public final static IHouse house華蓋 = new IHouseYearBranchImpl(華蓋) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun華蓋.apply(branch);
    }
  };

  public final static IHouse house咸池 = new IHouseYearBranchImpl(咸池) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun咸池.apply(branch);
    }
  };

  public final static IHouse house天德 = new IHouseYearBranchImpl(天德) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun天德.apply(branch);
    }
  };

  public final static IHouse house月德 = new IHouseYearBranchImpl(月德) {
    @Override
    public Branch getBranch(Branch branch) {
      return fun月德.apply(branch);
    }
  };

  public final static IHouse house天才 = new IHouseYearBranchMonthNumHourBranchImpl(天才) {
    @Override
    public Branch getBranch(Tuple3<Branch, Integer, Branch> t) {
      return fun天才.apply(t.v1() , t.v2() , t.v3());
    }
  };

  public final static IHouse house天壽 = new IHouseYearBranchMonthNumHourBranchImpl(天壽) {
    @Override
    public Branch getBranch(Tuple3<Branch, Integer, Branch> t) {
      return fun天壽.apply(t.v1() , t.v2() , t.v3());
    }
  };

  public final static IHouse house三台 = new IHouseMonthBranchDayNumImpl(三台) {
    @Override
    public Branch getBranch(Tuple2<Branch, Integer> t) {
      return fun三台.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house八座 = new IHouseMonthBranchDayNumImpl(八座) {
    @Override
    public Branch getBranch(Tuple2<Branch, Integer> t) {
      return fun八座.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house恩光 = new IHouseDayNumHourBranchImpl(恩光) {
    @Override
    public Branch getBranch(Tuple2<Integer, Branch> t) {
      return fun恩光.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house天貴 = new IHouseDayNumHourBranchImpl(天貴) {
    @Override
    public Branch getBranch(Tuple2<Integer, Branch> t) {
      return fun天貴.apply(t.v1() , t.v2());
    }
  };

  // =======↑↑↑======= 以上      雜曜 =======↑↑↑=======


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
    .build();

  public final static Map<ZStar , IHouse> map = set.stream()
    .collect(Collectors.toMap(IHouse::getStar, iHouse -> iHouse));

}
