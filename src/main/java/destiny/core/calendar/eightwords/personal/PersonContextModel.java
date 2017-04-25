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
import org.jooq.lambda.tuple.Tuple2;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 將 PersonContext 要呈現的資料都預先計算好（流年、大運...等），
 * 方便未來 View 端直接存取。不用在 View 端計算。
 */
public class PersonContextModel extends EightWordsContextModel {

  /** 性別 */
  protected final Gender gender;

  /** 總共要輸出的大運 */
  private final List<FortuneData> fortuneDatas;

  public PersonContextModel(Gender gender, EightWords eightWords, LocalDateTime lmt, Location location, String locationName, ChineseDate chineseDate, boolean dst, int gmtMinuteOffset, List<FortuneData> fortuneDatas, StemBranch risingStemBranch, Branch sunBranch, Branch moonBranch, Tuple2<SolarTerms, SolarTerms> prevNextMajorSolarTerms) {
    super(eightWords ,lmt , location, locationName, gmtMinuteOffset, dst, chineseDate, prevNextMajorSolarTerms.v1(), prevNextMajorSolarTerms.v2(), risingStemBranch, sunBranch, moonBranch);
    this.gender = gender;
    this.fortuneDatas = fortuneDatas;
  } // constructor

  public Gender getGender() {
    return gender;
  }

  public List<FortuneData> getFortuneDatas() {
    return Collections.unmodifiableList(fortuneDatas);
  }

}
