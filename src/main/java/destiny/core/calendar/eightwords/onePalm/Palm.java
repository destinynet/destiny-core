/**
 * Created by smallufo on 2015-05-17.
 */
package destiny.core.calendar.eightwords.onePalm;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import destiny.core.Gender;
import destiny.core.chinese.EarthlyBranches;
import destiny.utils.Tuple;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 達摩一掌金 資料結構
 */
public class Palm implements Serializable {

  private final Gender gender;

  private final EarthlyBranches year;

  private final EarthlyBranches month;

  private final EarthlyBranches day;

  private final EarthlyBranches hour;

  public enum Pillar { 年 , 月 , 日 , 時}

  private final Multimap<EarthlyBranches , Pillar> pillarMap = HashMultimap.create();

  public enum House {命 , 財帛 , 兄弟 , 田宅 , 男女 , 奴僕 , 配偶 , 疾厄 , 遷移 , 官祿 , 福德 , 相貌}

  public final BiMap<EarthlyBranches , House> houseMap;

  public Palm(Gender gender, EarthlyBranches year, EarthlyBranches month, EarthlyBranches day, EarthlyBranches hour , BiMap<EarthlyBranches , House> houseMap) {
    this.gender = gender;
    this.year = year;
    this.month = month;
    this.day = day;
    this.hour = hour;

    pillarMap.put(year, Pillar.年);
    pillarMap.put(month, Pillar.月);
    pillarMap.put(day, Pillar.日);
    pillarMap.put(hour, Pillar.時);

    this.houseMap = houseMap;
  }

  public Gender getGender() {
    return gender;
  }

  public EarthlyBranches getYear() {
    return year;
  }

  public EarthlyBranches getMonth() {
    return month;
  }

  public EarthlyBranches getDay() {
    return day;
  }

  public EarthlyBranches getHour() {
    return hour;
  }

  /**
   * 取得在某一地支宮位，包含了哪些「柱」 (年/月/日/時)
   */
  public Collection<Pillar> getPillars(EarthlyBranches branch) {
    return pillarMap.get(branch);
  }

  /** 取得此地支，是什麼宮 */
  public House getHouse(EarthlyBranches branch) {
    return houseMap.get(branch);
  }

  /** 與上面相反，取得此宮位於什麼地支 */
  public EarthlyBranches getBranch(House house) {
    return houseMap.inverse().get(house);
  }

  /**
   * 大運從掌中年上起月，男順、女逆，輪數至本生月起運。本生月所在宮為一運，下一宮為二運，而一運管10年。
   *
   * 大運盤，每運10年，從 1歲起. 1~10 , 11~20 , 21~30 ...
   * Map 的 key 為「運的開始」: 1 , 11 , 21 , 31 ...
   * @param count : 要算多少組大運
   */
  public Map<Integer , EarthlyBranches> getMajorFortunes(int count) {
    int positive = (gender==Gender.男 ? 1 : -1) ;

    return IntStream.range(1 , count+1).boxed()
      .map(i -> Tuple.of(
          (i - 1) * 10 + 1,
          EarthlyBranches.getEarthlyBranches(month.getIndex() + (i - 1) * positive)
        )
      )
      .collect(Collectors.toMap(Tuple::getFirst, Tuple::getSecond, (a, b) -> a, TreeMap::new));
  }

  /**
   * 小運從掌中年上起月，月上起日，男順、女逆，輪數至本生日起運。本生日所在宮為一歲運，下一宮為二歲運。
   * @param age 虛歲 , 從 1 開始
   */
  public EarthlyBranches getMinorFortunes(int age) {
    int positive = (gender==Gender.男 ? 1 : -1) ;
    return EarthlyBranches.getEarthlyBranches(day.getIndex() + (age-1)* positive);
  }

  /** 取得地支對應的「星」 (子 -> 天貴星) */
  public static String getStar(EarthlyBranches branch) {
    switch (branch) {
      case 子 : return "天貴";
      case 丑 : return "天厄";
      case 寅 : return "天權";
      case 卯 : return "天破";
      case 辰 : return "天奸";
      case 巳 : return "天文";
      case 午 : return "天福";
      case 未 : return "天驛";
      case 申 : return "天孤";
      case 酉 : return "天刃";
      case 戌 : return "天藝";
      case 亥 : return "天壽";
      default : throw new AssertionError(branch.toString());
    }
  }

  /** 取得地支對應的「道」 (子 -> 佛道) */
  public static String getDao(EarthlyBranches branch) {
    switch (branch) {
      case 子 : return "佛";
      case 丑 : return "鬼";
      case 寅 : return "人";
      case 卯 : return "畜生";
      case 辰 : return "修羅";
      case 巳 : return "仙";
      case 午 : return "佛";
      case 未 : return "鬼";
      case 申 : return "人";
      case 酉 : return "畜生";
      case 戌 : return "修羅";
      case 亥 : return "仙";
      default : throw new AssertionError(branch.toString());
    }
  }

  @Override
  public String toString() {
    return "[Palm " +
      "year=" + year +
      ", month=" + month +
      ", day=" + day +
      ", hour=" + hour +
      ']';
  }
}
