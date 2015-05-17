/**
 * Created by smallufo on 2015-05-17.
 */
package destiny.core.calendar.eightwords.onePalm;

import destiny.core.Gender;
import destiny.core.chinese.EarthlyBranches;
import destiny.utils.Tuple;

import java.io.Serializable;
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

  public Palm(Gender gender, EarthlyBranches year, EarthlyBranches month, EarthlyBranches day, EarthlyBranches hour) {
    this.gender = gender;
    this.year = year;
    this.month = month;
    this.day = day;
    this.hour = hour;
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
