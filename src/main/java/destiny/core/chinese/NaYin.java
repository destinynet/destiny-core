/**
 * Created by smallufo on 2015-05-12.
 */
package destiny.core.chinese;

import com.google.common.collect.ImmutableMap;
import destiny.utils.Tuple;

import java.io.Serializable;
import java.util.Map;

/**
 * 納音五行
 * 甲子乙丑海中金　丙寅丁卯爐中火　戊辰己巳大林木　庚午辛未路旁土
 * 壬申癸酉劍鋒金　甲戌乙亥山頭火　丙子丁丑澗下水　戊寅己卯城頭土
 * 庚辰辛巳白臘金　壬午癸未楊柳木　甲申乙酉井泉水　丙戌丁亥屋上土
 * 戊子己丑霹靂火　庚寅辛卯松柏木　壬辰癸巳長流水　甲午乙未砂中金
 * 丙申丁酉山下火　戊戌己亥平地木　庚子辛丑壁上土　壬寅癸卯金箔金
 * 甲辰乙巳覆燈火　丙午丁未天河水　戊申己酉大驛土　庚戌辛亥釵釧金
 * 壬子癸丑桑柘木　甲寅乙卯大溪水　丙辰丁巳砂中土　戊午己未天上火
 * 庚申辛酉石榴木　壬戌癸亥大海水
 */
public class NaYin implements Serializable {

  private final static Map<StemBranch, Tuple<FiveElement, String>> map = new ImmutableMap.Builder<StemBranch, Tuple<FiveElement, String>>() {{
    put(StemBranch.get(HeavenlyStems.甲 , EarthlyBranches.子), Tuple.of(FiveElement.金, "海中金"));
    put(StemBranch.get(HeavenlyStems.乙 , EarthlyBranches.丑), Tuple.of(FiveElement.金, "海中金"));
    put(StemBranch.get(HeavenlyStems.丙 , EarthlyBranches.寅), Tuple.of(FiveElement.火, "爐中火"));
    put(StemBranch.get(HeavenlyStems.丁 , EarthlyBranches.卯), Tuple.of(FiveElement.火, "爐中火"));
    put(StemBranch.get(HeavenlyStems.戊 , EarthlyBranches.辰), Tuple.of(FiveElement.木, "大林木"));
    put(StemBranch.get(HeavenlyStems.己 , EarthlyBranches.巳), Tuple.of(FiveElement.木, "大林木"));
    put(StemBranch.get(HeavenlyStems.庚 , EarthlyBranches.午), Tuple.of(FiveElement.土, "路旁土"));
    put(StemBranch.get(HeavenlyStems.辛 , EarthlyBranches.未), Tuple.of(FiveElement.土, "路旁土"));

    put(StemBranch.get(HeavenlyStems.壬 , EarthlyBranches.申), Tuple.of(FiveElement.金, "劍鋒金"));
    put(StemBranch.get(HeavenlyStems.癸 , EarthlyBranches.酉), Tuple.of(FiveElement.金, "劍鋒金"));
    put(StemBranch.get(HeavenlyStems.甲 , EarthlyBranches.戌), Tuple.of(FiveElement.火, "山頭火"));
    put(StemBranch.get(HeavenlyStems.乙 , EarthlyBranches.亥), Tuple.of(FiveElement.火, "山頭火"));
    put(StemBranch.get(HeavenlyStems.丙 , EarthlyBranches.子), Tuple.of(FiveElement.水, "澗下水"));
    put(StemBranch.get(HeavenlyStems.丁 , EarthlyBranches.丑), Tuple.of(FiveElement.水, "澗下水"));
    put(StemBranch.get(HeavenlyStems.戊 , EarthlyBranches.寅), Tuple.of(FiveElement.土, "城頭土"));
    put(StemBranch.get(HeavenlyStems.己 , EarthlyBranches.卯), Tuple.of(FiveElement.土, "城頭土"));

    put(StemBranch.get(HeavenlyStems.庚 , EarthlyBranches.辰), Tuple.of(FiveElement.金, "白臘金"));
    put(StemBranch.get(HeavenlyStems.辛 , EarthlyBranches.巳), Tuple.of(FiveElement.金, "白臘金"));
    put(StemBranch.get(HeavenlyStems.壬 , EarthlyBranches.午), Tuple.of(FiveElement.木, "楊柳木"));
    put(StemBranch.get(HeavenlyStems.癸 , EarthlyBranches.未), Tuple.of(FiveElement.木, "楊柳木"));
    put(StemBranch.get(HeavenlyStems.甲 , EarthlyBranches.申), Tuple.of(FiveElement.水, "井泉水"));
    put(StemBranch.get(HeavenlyStems.乙 , EarthlyBranches.酉), Tuple.of(FiveElement.水, "井泉水"));
    put(StemBranch.get(HeavenlyStems.丙 , EarthlyBranches.戌), Tuple.of(FiveElement.土, "屋上土"));
    put(StemBranch.get(HeavenlyStems.丁 , EarthlyBranches.亥), Tuple.of(FiveElement.土, "屋上土"));

    put(StemBranch.get(HeavenlyStems.戊 , EarthlyBranches.子), Tuple.of(FiveElement.火, "霹靂火"));
    put(StemBranch.get(HeavenlyStems.己 , EarthlyBranches.丑), Tuple.of(FiveElement.火, "霹靂火"));
    put(StemBranch.get(HeavenlyStems.庚 , EarthlyBranches.寅), Tuple.of(FiveElement.木, "松柏木"));
    put(StemBranch.get(HeavenlyStems.辛 , EarthlyBranches.卯), Tuple.of(FiveElement.木, "松柏木"));
    put(StemBranch.get(HeavenlyStems.壬 , EarthlyBranches.辰), Tuple.of(FiveElement.水, "長流水"));
    put(StemBranch.get(HeavenlyStems.癸 , EarthlyBranches.巳), Tuple.of(FiveElement.水, "長流水"));
    put(StemBranch.get(HeavenlyStems.甲 , EarthlyBranches.午), Tuple.of(FiveElement.金, "砂中金"));
    put(StemBranch.get(HeavenlyStems.乙 , EarthlyBranches.未), Tuple.of(FiveElement.金, "砂中金"));

    put(StemBranch.get(HeavenlyStems.丙 , EarthlyBranches.申), Tuple.of(FiveElement.火, "山下火"));
    put(StemBranch.get(HeavenlyStems.丁 , EarthlyBranches.酉), Tuple.of(FiveElement.火, "山下火"));
    put(StemBranch.get(HeavenlyStems.戊 , EarthlyBranches.戌), Tuple.of(FiveElement.木, "平地木"));
    put(StemBranch.get(HeavenlyStems.己 , EarthlyBranches.亥), Tuple.of(FiveElement.木, "平地木"));
    put(StemBranch.get(HeavenlyStems.庚 , EarthlyBranches.子), Tuple.of(FiveElement.土, "壁上土"));
    put(StemBranch.get(HeavenlyStems.辛 , EarthlyBranches.丑), Tuple.of(FiveElement.土, "壁上土"));
    put(StemBranch.get(HeavenlyStems.壬 , EarthlyBranches.寅), Tuple.of(FiveElement.金, "金箔金"));
    put(StemBranch.get(HeavenlyStems.癸 , EarthlyBranches.卯), Tuple.of(FiveElement.金, "金箔金"));

    put(StemBranch.get(HeavenlyStems.甲 , EarthlyBranches.辰), Tuple.of(FiveElement.火, "覆燈火"));
    put(StemBranch.get(HeavenlyStems.乙 , EarthlyBranches.巳), Tuple.of(FiveElement.火, "覆燈火"));
    put(StemBranch.get(HeavenlyStems.丙 , EarthlyBranches.午), Tuple.of(FiveElement.水, "天河水"));
    put(StemBranch.get(HeavenlyStems.丁 , EarthlyBranches.未), Tuple.of(FiveElement.水, "天河水"));
    put(StemBranch.get(HeavenlyStems.戊 , EarthlyBranches.申), Tuple.of(FiveElement.土, "大驛土"));
    put(StemBranch.get(HeavenlyStems.己 , EarthlyBranches.酉), Tuple.of(FiveElement.土, "大驛土"));
    put(StemBranch.get(HeavenlyStems.庚 , EarthlyBranches.戌), Tuple.of(FiveElement.金, "釵釧金"));
    put(StemBranch.get(HeavenlyStems.辛 , EarthlyBranches.亥), Tuple.of(FiveElement.金, "釵釧金"));

    put(StemBranch.get(HeavenlyStems.壬 , EarthlyBranches.子), Tuple.of(FiveElement.木, "桑柘木"));
    put(StemBranch.get(HeavenlyStems.癸 , EarthlyBranches.丑), Tuple.of(FiveElement.木, "桑柘木"));
    put(StemBranch.get(HeavenlyStems.甲 , EarthlyBranches.寅), Tuple.of(FiveElement.水, "大溪水"));
    put(StemBranch.get(HeavenlyStems.乙 , EarthlyBranches.卯), Tuple.of(FiveElement.水, "大溪水"));
    put(StemBranch.get(HeavenlyStems.丙 , EarthlyBranches.辰), Tuple.of(FiveElement.土, "砂中土"));
    put(StemBranch.get(HeavenlyStems.丁 , EarthlyBranches.巳), Tuple.of(FiveElement.土, "砂中土"));
    put(StemBranch.get(HeavenlyStems.戊 , EarthlyBranches.午), Tuple.of(FiveElement.火, "天上火"));
    put(StemBranch.get(HeavenlyStems.己 , EarthlyBranches.未), Tuple.of(FiveElement.火, "天上火"));

    put(StemBranch.get(HeavenlyStems.庚 , EarthlyBranches.申), Tuple.of(FiveElement.木, "石榴木"));
    put(StemBranch.get(HeavenlyStems.辛 , EarthlyBranches.酉), Tuple.of(FiveElement.木, "石榴木"));
    put(StemBranch.get(HeavenlyStems.壬 , EarthlyBranches.戌), Tuple.of(FiveElement.水, "大海水"));
    put(StemBranch.get(HeavenlyStems.癸 , EarthlyBranches.亥), Tuple.of(FiveElement.水, "大海水"));
  }}.build();

  public static FiveElement getFiveElement(StemBranch sb) {
    return map.get(sb).getFirst();
  }

  /** 詳情 , 三個字 , 例如「海中金」 */
  public static String getDesc(StemBranch sb) {
    return map.get(sb).getSecond();
  }
}
