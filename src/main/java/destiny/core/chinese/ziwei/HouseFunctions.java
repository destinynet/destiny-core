/**
 * Created by smallufo on 2017-04-13.
 */
package destiny.core.chinese.ziwei;

import com.google.common.collect.ImmutableMap;
import destiny.core.chinese.Branch;
import destiny.core.chinese.Stem;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Map;

import static destiny.core.chinese.ziwei.LuckyStar.*;
import static destiny.core.chinese.ziwei.MainStar.*;
import static destiny.core.chinese.ziwei.UnluckyStar.*;
import static destiny.core.chinese.ziwei.ZiweiIF.*;

public class HouseFunctions {

  public final static IHouse house紫微 = new IHouseMainStarImpl() {
    @Override
    public Branch getBranch(Tuple2<Integer, Integer> t) {
      return fun紫微.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house天機 = new IHouseMainStarImpl() {
    @Override
    public Branch getBranch(Tuple2<Integer, Integer> t) {
      return fun天機.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house太陽 = new IHouseMainStarImpl() {
    @Override
    public Branch getBranch(Tuple2<Integer, Integer> t) {
      return fun太陽.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house武曲 = new IHouseMainStarImpl() {
    @Override
    public Branch getBranch(Tuple2<Integer, Integer> t) {
      return fun武曲.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house天同 = new IHouseMainStarImpl() {
    @Override
    public Branch getBranch(Tuple2<Integer, Integer> t) {
      return fun天同.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house廉貞 = new IHouseMainStarImpl() {
    @Override
    public Branch getBranch(Tuple2<Integer, Integer> t) {
      return fun廉貞.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house天府 = new IHouseMainStarImpl() {
    @Override
    public Branch getBranch(Tuple2<Integer, Integer> t) {
      return fun天府.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house太陰 = new IHouseMainStarImpl() {
    @Override
    public Branch getBranch(Tuple2<Integer, Integer> t) {
      return fun太陰.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house貪狼 = new IHouseMainStarImpl() {
    @Override
    public Branch getBranch(Tuple2<Integer, Integer> t) {
      return fun貪狼.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house巨門 = new IHouseMainStarImpl() {
    @Override
    public Branch getBranch(Tuple2<Integer, Integer> t) {
      return fun巨門.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house天相 = new IHouseMainStarImpl() {
    @Override
    public Branch getBranch(Tuple2<Integer, Integer> t) {
      return fun天相.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house天梁 = new IHouseMainStarImpl() {
    @Override
    public Branch getBranch(Tuple2<Integer, Integer> t) {
      return fun天梁.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house七殺 = new IHouseMainStarImpl() {
    @Override
    public Branch getBranch(Tuple2<Integer, Integer> t) {
      return fun七殺.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house破軍 = new IHouseMainStarImpl() {
    @Override
    public Branch getBranch(Tuple2<Integer, Integer> t) {
      return fun破軍.apply(t.v1() , t.v2());
    }
  };

  // =======↑↑↑======= 以上 14 顆主星 =======↑↑↑=======

  // =======↓↓↓======= 以下  8 顆吉星 =======↓↓↓=======
  
  public final static IHouse house文昌 = new IHouseHourBranchImpl() {
    @Override
    public Branch getBranch(Branch branch) {
      return fun文昌.apply(branch);
    }
  };

  public final static IHouse house文曲 = new IHouseHourBranchImpl() {
    @Override
    public Branch getBranch(Branch branch) {
      return fun文曲.apply(branch);
    }
  };

  public final static IHouse house左輔 = new IHouseHourBranchImpl() {
    @Override
    public Branch getBranch(Branch branch) {
      return fun左輔.apply(branch);
    }
  };
  
  public final static IHouse house右弼 = new IHouseHourBranchImpl() {
    @Override
    public Branch getBranch(Branch branch) {
      return fun右弼.apply(branch);
    }
  };
  
  public final static IHouse house天魁 = new IHouseYearStemImpl() {
    @Override
    public Branch getBranch(Stem stem) {
      return fun天魁.apply(stem);
    }
  };
  
  public final static IHouse house天鉞 = new IHouseYearStemImpl() {
    @Override
    public Branch getBranch(Stem stem) {
      return fun天鉞.apply(stem);
    }
  };
  
  public final static IHouse house祿存 = new IHouseYearStemImpl() {
    @Override
    public Branch getBranch(Stem stem) {
      return fun祿存.apply(stem);
    }
  };
  
  public final static IHouse house天馬 = new IHouseMonthBranchImpl() {
    @Override
    public Branch getBranch(Branch branch) {
      return fun天馬.apply(branch);
    }
  };
  
  // =======↑↑↑======= 以上  8 顆吉星 =======↑↑↑=======
  
  // =======↓↓↓======= 以下  6 顆兇星 =======↓↓↓=======

  public final static IHouse house擎羊 = new IHouseYearStemImpl() {
    @Override
    public Branch getBranch(Stem stem) {
      return fun擎羊.apply(stem);
    }
  };

  public final static IHouse house陀羅 = new IHouseYearStemImpl() {
    @Override
    public Branch getBranch(Stem stem) {
      return fun陀羅.apply(stem);
    }
  };

  public final static IHouse house火星_全書 = new IHouseYearBranchImpl() {
    @Override
    public Branch getBranch(Branch branch) {
      return fun火星_全書.apply(branch);
    }
  };


  public final static IHouse house火星_全集 = new IHouseYearBranchHourBranchImpl() {
    @Override
    public Branch getBranch(Tuple2<Branch, Branch> t) {
      return fun火星_全集.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house鈴星_全書 = new IHouseYearBranchImpl() {
    @Override
    public Branch getBranch(Branch branch) {
      return fun鈴星_全書.apply(branch);
    }
  };

  public final static IHouse house鈴星_全集 = new IHouseYearBranchHourBranchImpl() {
    @Override
    public Branch getBranch(Tuple2<Branch, Branch> t) {
      return fun鈴星_全集.apply(t.v1() , t.v2());
    }
  };

  public final static IHouse house地劫 = new IHouseHourBranchImpl() {
    @Override
    public Branch getBranch(Branch branch) {
      return fun地劫.apply(branch);
    }
  };

  public final static IHouse house地空 = new IHouseHourBranchImpl() {
    @Override
    public Branch getBranch(Branch branch) {
      return fun地空.apply(branch);
    }
  };
  
  public final static Map<ZStar , IHouse> map = new ImmutableMap.Builder<ZStar, IHouse>()
    // 14主星
    .put(紫微, house紫微)
    .put(天機, house天機)
    .put(太陽, house太陽)
    .put(武曲, house武曲)
    .put(天同, house天同)
    .put(廉貞, house廉貞)
    .put(天府, house天府)
    .put(太陰, house太陰)
    .put(貪狼, house貪狼)
    .put(巨門, house巨門)
    .put(天相, house天相)
    .put(天梁, house天梁)
    .put(七殺, house七殺)
    .put(破軍, house破軍)

    // 八吉星
    .put(文昌 , house文昌)
    .put(文曲 , house文曲)
    .put(左輔 , house左輔)
    .put(右弼 , house右弼)
    .put(天魁 , house天魁)
    .put(天鉞 , house天鉞)
    .put(祿存 , house祿存)
    .put(天馬 , house天馬)

    // 六兇星
    .put(擎羊 , house擎羊)
    .put(陀羅 , house陀羅)
    .put(火星 , house火星_全集)
    .put(鈴星 , house鈴星_全集)
    .put(地劫 , house地劫)
    .put(地空 , house地空)

    .build();


}
