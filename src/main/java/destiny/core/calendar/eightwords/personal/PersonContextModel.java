/**
 * Created by smallufo on 2014-11-30.
 */
package destiny.core.calendar.eightwords.personal;

import destiny.core.Gender;
import destiny.core.calendar.Location;
import destiny.core.calendar.SolarTerms;
import destiny.core.calendar.chinese.ChineseDate;
import destiny.core.calendar.eightwords.EightWords;
import destiny.core.calendar.eightwords.EightWordsContextModel;
import destiny.core.chinese.Branch;
import destiny.core.chinese.StemBranch;
import kotlin.Pair;

import java.time.chrono.ChronoLocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 將 PersonContext 要呈現的資料都預先計算好（流年、大運...等），
 * 方便未來 View 端直接存取。不用在 View 端計算。
 */
public class PersonContextModel extends EightWordsContextModel {

  /** 性別 */
  protected final Gender gender;

  /** 總共要輸出的大運 */
  private final List<FortuneData> fortuneDatas;

  /** 歲數(可能是虛歲)，每歲的起訖時刻 */
  private final Map<Integer , Pair<Double , Double>> ageMap;

  public PersonContextModel(Gender gender, EightWords eightWords, ChronoLocalDateTime lmt, Location location,
                            String place, ChineseDate chineseDate,
                            List<FortuneData> fortuneDatas, StemBranch risingStemBranch, Branch sunBranch,
                            Branch moonBranch, Pair<SolarTerms, SolarTerms> prevNextMajorSolarTerms,
                            Map<Integer, Pair<Double, Double>> ageMap) {
    super(eightWords ,lmt , location, place, chineseDate,
      prevNextMajorSolarTerms.getFirst(),
      prevNextMajorSolarTerms.getSecond(),
      risingStemBranch, sunBranch, moonBranch);
    this.gender = gender;
    this.fortuneDatas = fortuneDatas;
    this.ageMap = ageMap;
  } // constructor

  public Gender getGender() {
    return gender;
  }

  public List<FortuneData> getFortuneDatas() {
    return Collections.unmodifiableList(fortuneDatas);
  }

  public Map<Integer, Pair<Double, Double>> getAgeMap() {
    return ageMap;
  }
}
