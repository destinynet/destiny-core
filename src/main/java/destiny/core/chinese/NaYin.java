/**
 * Created by smallufo on 2015-05-12.
 */
package destiny.core.chinese;

import com.google.common.collect.ImmutableMap;
import org.jooq.lambda.tuple.Tuple2;

import java.io.Serializable;
import java.util.Map;

import static destiny.core.chinese.StemBranch.*;
import static org.jooq.lambda.tuple.Tuple.tuple;

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

  private final static Map<StemBranch, Tuple2<FiveElement, String>> map
    = new ImmutableMap.Builder<StemBranch, Tuple2<FiveElement, String>>()
  {{
    put(甲子, tuple(FiveElement.金, "海中金"));
    put(乙丑, tuple(FiveElement.金, "海中金"));
    put(丙寅, tuple(FiveElement.火, "爐中火"));
    put(丁卯, tuple(FiveElement.火, "爐中火"));
    put(戊辰, tuple(FiveElement.木, "大林木"));
    put(己巳, tuple(FiveElement.木, "大林木"));
    put(庚午, tuple(FiveElement.土, "路旁土"));
    put(辛未, tuple(FiveElement.土, "路旁土"));
    put(壬申, tuple(FiveElement.金, "劍鋒金"));
    put(癸酉, tuple(FiveElement.金, "劍鋒金"));
    put(甲戌, tuple(FiveElement.火, "山頭火"));
    put(乙亥, tuple(FiveElement.火, "山頭火"));
    put(丙子, tuple(FiveElement.水, "澗下水"));
    put(丁丑, tuple(FiveElement.水, "澗下水"));
    put(戊寅, tuple(FiveElement.土, "城頭土"));
    put(己卯, tuple(FiveElement.土, "城頭土"));
    put(庚辰, tuple(FiveElement.金, "白臘金"));
    put(辛巳, tuple(FiveElement.金, "白臘金"));
    put(壬午, tuple(FiveElement.木, "楊柳木"));
    put(癸未, tuple(FiveElement.木, "楊柳木"));
    put(甲申, tuple(FiveElement.水, "井泉水"));
    put(乙酉, tuple(FiveElement.水, "井泉水"));
    put(丙戌, tuple(FiveElement.土, "屋上土"));
    put(丁亥, tuple(FiveElement.土, "屋上土"));
    put(戊子, tuple(FiveElement.火, "霹靂火"));
    put(己丑, tuple(FiveElement.火, "霹靂火"));
    put(庚寅, tuple(FiveElement.木, "松柏木"));
    put(辛卯, tuple(FiveElement.木, "松柏木"));
    put(壬辰, tuple(FiveElement.水, "長流水"));
    put(癸巳, tuple(FiveElement.水, "長流水"));
    put(甲午, tuple(FiveElement.金, "砂中金"));
    put(乙未, tuple(FiveElement.金, "砂中金"));
    put(丙申, tuple(FiveElement.火, "山下火"));
    put(丁酉, tuple(FiveElement.火, "山下火"));
    put(戊戌, tuple(FiveElement.木, "平地木"));
    put(己亥, tuple(FiveElement.木, "平地木"));
    put(庚子, tuple(FiveElement.土, "壁上土"));
    put(辛丑, tuple(FiveElement.土, "壁上土"));
    put(壬寅, tuple(FiveElement.金, "金箔金"));
    put(癸卯, tuple(FiveElement.金, "金箔金"));
    put(甲辰, tuple(FiveElement.火, "覆燈火"));
    put(乙巳, tuple(FiveElement.火, "覆燈火"));
    put(丙午, tuple(FiveElement.水, "天河水"));
    put(丁未, tuple(FiveElement.水, "天河水"));
    put(戊申, tuple(FiveElement.土, "大驛土"));
    put(己酉, tuple(FiveElement.土, "大驛土"));
    put(庚戌, tuple(FiveElement.金, "釵釧金"));
    put(辛亥, tuple(FiveElement.金, "釵釧金"));
    put(壬子, tuple(FiveElement.木, "桑柘木"));
    put(癸丑, tuple(FiveElement.木, "桑柘木"));
    put(甲寅, tuple(FiveElement.水, "大溪水"));
    put(乙卯, tuple(FiveElement.水, "大溪水"));
    put(丙辰, tuple(FiveElement.土, "砂中土"));
    put(丁巳, tuple(FiveElement.土, "砂中土"));
    put(戊午, tuple(FiveElement.火, "天上火"));
    put(己未, tuple(FiveElement.火, "天上火"));
    put(庚申, tuple(FiveElement.木, "石榴木"));
    put(辛酉, tuple(FiveElement.木, "石榴木"));
    put(壬戌, tuple(FiveElement.水, "大海水"));
    put(癸亥, tuple(FiveElement.水, "大海水"));
  }}.build();

  public static FiveElement getFiveElement(StemBranch sb) {
    return map.get(sb).v1();
  }

  /** 詳情 , 三個字 , 例如「海中金」 */
  public static String getDesc(StemBranch sb) {
    return map.get(sb).v2();
  }
}
