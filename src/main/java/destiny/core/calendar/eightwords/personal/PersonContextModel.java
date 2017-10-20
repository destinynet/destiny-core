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
import destiny.core.chinese.FortuneOutput;
import destiny.core.chinese.StemBranch;
import org.jooq.lambda.tuple.Tuple2;

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

  /** 大運輸出格式 */
  private final FortuneOutput fortuneOutput;

  /** 總共要輸出的大運 */
  private final List<FortuneData> fortuneDatas;

  /** 虛歲，每歲的起訖時分 */
  private final Map<Integer , Tuple2<ChronoLocalDateTime , ChronoLocalDateTime>> vageMap;

  public PersonContextModel(Gender gender, EightWords eightWords, ChronoLocalDateTime lmt, Location location, String locationName, ChineseDate chineseDate, boolean dst, int gmtMinuteOffset, List<FortuneData> fortuneDatas, StemBranch risingStemBranch, Branch sunBranch, Branch moonBranch, Tuple2<SolarTerms, SolarTerms> prevNextMajorSolarTerms, FortuneOutput fortuneOutput, Map<Integer, Tuple2<ChronoLocalDateTime, ChronoLocalDateTime>> vageMap) {
    super(eightWords ,lmt , location, locationName, gmtMinuteOffset, dst, chineseDate, prevNextMajorSolarTerms.v1(), prevNextMajorSolarTerms.v2(), risingStemBranch, sunBranch, moonBranch);
    this.gender = gender;
    this.fortuneDatas = fortuneDatas;
    this.fortuneOutput = fortuneOutput;
    this.vageMap = vageMap;
  } // constructor

  public Gender getGender() {
    return gender;
  }

  public List<FortuneData> getFortuneDatas() {
    return Collections.unmodifiableList(fortuneDatas);
  }

  public FortuneOutput getFortuneOutput() {
    return fortuneOutput;
  }
}
