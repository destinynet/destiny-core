/**
 * Created by smallufo on 2015-05-12.
 */
package destiny.core.chinese;

import com.google.common.collect.ImmutableMap;

import java.io.Serializable;
import java.util.Map;

/**
 * 太玄數
 *
 * 甲己子午九，乙庚丑未八，丙辛寅申七，丁壬卯酉六，戊癸辰戌五，巳亥單四數
 * 深入探討資料： @see <a href="http://tieba.baidu.com/p/2236977909">http://tieba.baidu.com/p/2236977909</a>
 */
public class TaiXuan implements Serializable {

  private final static Map<HeavenlyStems , Integer> stemMap = new ImmutableMap.Builder<HeavenlyStems , Integer>() {{
    put(HeavenlyStems.甲, 9);
    put(HeavenlyStems.乙, 8);
    put(HeavenlyStems.丙, 7);
    put(HeavenlyStems.丁, 6);
    put(HeavenlyStems.戊, 5);
    put(HeavenlyStems.己, 9);
    put(HeavenlyStems.庚, 8);
    put(HeavenlyStems.辛, 7);
    put(HeavenlyStems.壬, 6);
    put(HeavenlyStems.癸, 5);
  }}.build();

  private final static Map<EarthlyBranches , Integer> branchMap = new ImmutableMap.Builder<EarthlyBranches, Integer>() {{
    put(EarthlyBranches.子 , 9);
    put(EarthlyBranches.丑 , 8);
    put(EarthlyBranches.寅 , 7);
    put(EarthlyBranches.卯 , 6);
    put(EarthlyBranches.辰 , 5);
    put(EarthlyBranches.巳 , 4);
    put(EarthlyBranches.午 , 9);
    put(EarthlyBranches.未 , 8);
    put(EarthlyBranches.申 , 7);
    put(EarthlyBranches.酉 , 6);
    put(EarthlyBranches.戌 , 5);
    put(EarthlyBranches.亥 , 4);
  }}.build();


  public static int get(HeavenlyStems stem) {
    return stemMap.get(stem);
  }

  public static int get(EarthlyBranches branch) {
    return branchMap.get(branch);
  }
}
