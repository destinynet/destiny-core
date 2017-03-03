/**
 * Created by smallufo on 2015-05-17.
 */
package destiny.core.chinese.onePalm;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import destiny.core.Gender;
import destiny.core.chinese.Branch;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

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

  private final Branch year;

  private final Branch month;

  private final Branch day;

  private final Branch hour;

  public enum Pillar { 年 , 月 , 日 , 時}

  /** 12個宮位，每個宮位，各有哪些「柱」 */
  private final Multimap<Branch, Pillar> pillarMap = HashMultimap.create();

  public enum House {命 , 財帛 , 兄弟 , 田宅 , 男女 , 奴僕 , 配偶 , 疾厄 , 遷移 , 官祿 , 福德 , 相貌}

  public final BiMap<Branch, House> houseMap;

  public Palm(Gender gender, Branch year, Branch month, Branch day, Branch hour , BiMap<Branch, House> houseMap) {
    this.gender = gender;
    this.year = year;
    this.month = month;
    this.day = day;
    this.hour = hour;
    this.houseMap = houseMap;

    pillarMap.put(year, Pillar.年);
    pillarMap.put(month, Pillar.月);
    pillarMap.put(day, Pillar.日);
    pillarMap.put(hour, Pillar.時);
  }

  protected Palm(Palm other) {
    this.gender = other.gender;
    this.year = other.year;
    this.month = other.month;
    this.day = other.day;
    this.hour = other.hour;
    this.houseMap = other.houseMap;

    this.pillarMap.putAll(other.pillarMap);
  }

  public Gender getGender() {
    return gender;
  }

  public Branch getYear() {
    return year;
  }

  public Branch getMonth() {
    return month;
  }

  public Branch getDay() {
    return day;
  }

  public Branch getHour() {
    return hour;
  }

  /**
   * 取得在某一地支宮位，包含了哪些「柱」 (年/月/日/時)
   */
  public Collection<Pillar> getPillars(Branch branch) {
    return pillarMap.get(branch);
  }

  /**
   * 取得哪些宮位有「柱」坐落其中，列出來
   * @return
   */
  public Map<Branch, Collection<Pillar>> getNonEmptyPillars() {
    return pillarMap.asMap().entrySet().stream()
      .filter(entry -> entry.getValue().size() > 0)
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  /**
   * 取得此柱，在哪個地支
   */
  public Branch getBranch(Pillar pillar) {
    switch (pillar) {
      case 年 : return getYear();
      case 月 : return getMonth();
      case 日 : return getDay();
      case 時 : return getHour();
      default : throw new AssertionError(pillar.toString());
    }
  }

  /** 取得此地支，是什麼宮 */
  public House getHouse(Branch branch) {
    return houseMap.get(branch);
  }

  /** 與上面相反，取得此宮位於什麼地支 */
  public Branch getBranch(House house) {
    return houseMap.inverse().get(house);
  }

  /**
   * 大運從掌中年上起月，男順、女逆，輪數至本生月起運。本生月所在宮為一運，下一宮為二運，而一運管10年。
   *
   * 大運盤，每運10年，從 1歲起. 1~10 , 11~20 , 21~30 ...
   * Map 的 key 為「運的開始」: 1 , 11 , 21 , 31 ...
   * @param count : 要算多少組大運
   */
  public Map<Integer , Branch> getMajorFortunes(int count) {
    int positive = (gender==Gender.男 ? 1 : -1) ;

    return IntStream.range(1 , count+1).boxed()
      .map(i -> ImmutablePair.of((i - 1) * 10 + 1, Branch.get(month.getIndex() + (i - 1) * positive))
      )
      .collect(Collectors.toMap(Pair::getLeft, Pair::getRight, (a, b) -> a, TreeMap::new));
  }

  /**
   * 小運從掌中年上起月，月上起日，男順、女逆，輪數至本生日起運。本生日所在宮為一歲運，下一宮為二歲運。
   * @param age 虛歲 , 從 1 開始
   */
  public Branch getMinorFortunes(int age) {
    int positive = (gender==Gender.男 ? 1 : -1) ;
    return Branch.get(day.getIndex() + (age - 1) * positive);
  }

  /** 取得地支對應的「星」 (子 -> 天貴星) */
  public static String getStar(Branch branch) {
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
  public static String getDao(Branch branch) {
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
