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

  private final static Map<Stem, Integer> stemMap = new ImmutableMap.Builder<Stem, Integer>() {{
    put(Stem.甲, 9);
    put(Stem.乙, 8);
    put(Stem.丙, 7);
    put(Stem.丁, 6);
    put(Stem.戊, 5);
    put(Stem.己, 9);
    put(Stem.庚, 8);
    put(Stem.辛, 7);
    put(Stem.壬, 6);
    put(Stem.癸, 5);
  }}.build();

  private final static Map<Branch, Integer> branchMap = new ImmutableMap.Builder<Branch, Integer>() {{
    put(Branch.子 , 9);
    put(Branch.丑 , 8);
    put(Branch.寅 , 7);
    put(Branch.卯 , 6);
    put(Branch.辰 , 5);
    put(Branch.巳 , 4);
    put(Branch.午 , 9);
    put(Branch.未 , 8);
    put(Branch.申 , 7);
    put(Branch.酉 , 6);
    put(Branch.戌 , 5);
    put(Branch.亥 , 4);
  }}.build();


  public static int get(Stem stem) {
    return stemMap.get(stem);
  }

  public static int get(Branch branch) {
    return branchMap.get(branch);
  }
}
