/**
 * Created by smallufo on 2015-05-17.
 */
package destiny.core.calendar.eightwords.onePalm;

import destiny.core.Gender;
import destiny.core.calendar.chinese.ChineseDateHour;
import destiny.core.chinese.EarthlyBranches;

import java.util.Map;
import java.util.TreeMap;

public interface PalmIF {

  /** 本命盤 */
  default Palm getPalm(Gender gender, EarthlyBranches yearBranch, boolean leap, int month, int day, EarthlyBranches hourBranch) {
    int positive = (gender==Gender.男 ? 1 : -1) ;

    int realMonth = month ;
    if (leap && day > 15)  // 若為閏月，15日以後算下個月
      realMonth++;

    // 年上起月
    EarthlyBranches monthBranch = EarthlyBranches.getEarthlyBranches(yearBranch.getIndex() + ((realMonth-1) * positive));

    // 月上起日
    EarthlyBranches dayBranch = EarthlyBranches.getEarthlyBranches(monthBranch.getIndex() + (day-1)* positive);

    // 日上起時
    EarthlyBranches hour = EarthlyBranches.getEarthlyBranches(dayBranch.getIndex() + ((hourBranch.getIndex())* positive));

    return new Palm(gender, yearBranch , monthBranch , dayBranch , hour);
  }

  /** 本命盤 */
  default Palm getPalm(Gender gender , ChineseDateHour chineseDateHour) {
    return getPalm(gender ,
      chineseDateHour.getYear().getBranch() , chineseDateHour.isLeapMonth() , chineseDateHour.getMonth() ,
      chineseDateHour.getDay() ,
      chineseDateHour.getHourBranch()
    );
  }

  /**
   *
   * 大運從掌中年上起月，男順、女逆，輪數至本生月起運。本生月所在宮為一運，下一宮為二運，而一運管10年。
   *
   *
   * 女則逆行，也從子（天貴宮）上起正月，三月則落在戌（天藝宮）上，戌為初運（1－10歲），酉（天刃宮）為二運（11－20歲）也，餘此類推。
   *
   * 大運盤，每運10年，從 1歲起. 1~10 , 11~20 , 21~30 ...
   * Map 的 key 為「運的開始」: 1 , 11 , 21 , 31 ...
   * @param count : 要算多少組大運
   * */
  default Map<Integer , EarthlyBranches> getMajorFortunes(Gender gender , EarthlyBranches yearBranch , int month , int count) {
    int positive = (gender==Gender.男 ? 1 : -1) ;

    // 年上起月
    EarthlyBranches monthBranch = EarthlyBranches.getEarthlyBranches(yearBranch.getIndex() + ((month-1) * positive));

    Map<Integer , EarthlyBranches> map = new TreeMap<>();

    for(int i = 1 ; i <= count ; i++) {
      map.put((i-1)* 10 + 1 , EarthlyBranches.getEarthlyBranches(monthBranch.getIndex() + (i-1)*positive ));
    }
    return map;
  }

  /**
   * 計算命宮
   * 以「時」的宮位為起點，男順女逆，數至「卯」，其所在的宮位就是命宮
   * 例如，女命，辰時在亥宮。   辰到卯， 相隔 11 .  而從亥宮逆數 11 , 到子宮，則為命宮
   */
  default EarthlyBranches getRising(Palm palm , EarthlyBranches hour) {
    int positive = (palm.getGender()==Gender.男 ? 1 : -1) ;

    int gap = EarthlyBranches.卯.getIndex() - hour.getIndex();
    if (gap < 0)
      gap = gap + 12;

    EarthlyBranches rising = EarthlyBranches.getEarthlyBranches(palm.getHour().getIndex() + gap * positive);
    return rising;
  }
}
