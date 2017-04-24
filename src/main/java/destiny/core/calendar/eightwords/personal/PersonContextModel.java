/**
 * Created by smallufo on 2014-11-30.
 */
package destiny.core.calendar.eightwords.personal;

import destiny.core.Gender;
import destiny.core.calendar.Location;
import destiny.core.calendar.SolarTerms;
import destiny.core.calendar.chinese.ChineseDate;
import destiny.core.calendar.eightwords.EightWords;
import destiny.core.chinese.Branch;
import destiny.core.chinese.StemBranch;
import org.jooq.lambda.tuple.Tuple2;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 將 PersonContext 要呈現的資料都預先計算好（流年、大運...等），方便未來 View 端直接存取。不用在 View 端計算。
 */
public class PersonContextModel implements Serializable {

  private final Gender gender;

  private final EightWords eightWords;
  private final PersonContext personContext;

  private final LocalDateTime lmt;

  /** 農曆 */
  private final ChineseDate chineseDate;


  private final Location location;

  /** 地點名稱 */
  private String locationName = "";

  private final int gmtMinuteOffset;

  /** 日光節約 */
  private final boolean dst;

  /** 總共要輸出的大運 */
  private final List<FortuneData> fortuneDatas ;//= new ArrayList<>();

  /** 上一個「節」 */
  private final SolarTerms prevMajorSolarTerms;

  /** 下一個「節」 */
  private final SolarTerms nextMajorSolarTerms;

  /** 命宮 (上升星座) */
  private final StemBranch risingStemBranch;

  /** 太陽位置 */
  private final Branch sunBranch;

  /** 月亮位置 */
  private final Branch moonBranch;


  public PersonContextModel(Gender gender, EightWords eightWords, PersonContext context, LocalDateTime lmt, Location location, String locationName, ChineseDate chineseDate, boolean dst, int gmtMinuteOffset, List<FortuneData> fortuneDatas, StemBranch risingStemBranch, Branch sunBranch, Branch moonBranch, Tuple2<SolarTerms, SolarTerms> prevNextMajorSolarTerms) {
    this.gender = gender;
    this.eightWords = eightWords;
    this.personContext = context;
    this.lmt = lmt;
    this.fortuneDatas = fortuneDatas;
    this.location = location;
    this.chineseDate = chineseDate;
    this.locationName = locationName;
    this.dst = dst;
    this.gmtMinuteOffset = gmtMinuteOffset;
    // 命宮干支
    this.risingStemBranch = risingStemBranch;
    this.sunBranch = sunBranch;
    this.moonBranch = moonBranch;

    this.prevMajorSolarTerms = prevNextMajorSolarTerms.v1();
    this.nextMajorSolarTerms = prevNextMajorSolarTerms.v2();
  } // constructor

  public Gender getGender() {
    return gender;
  }

  public EightWords getEightWords() {
    return eightWords;
  }

  @Deprecated
  public PersonContext getPersonContext() {
    return personContext;
  }

  public LocalDateTime getLmt() {
    return lmt;
  }

  public ChineseDate getChineseDate() {
    return chineseDate;
  }

  public int getGmtMinuteOffset() {
    return gmtMinuteOffset;
  }

  public List<FortuneData> getFortuneDatas() {
    return Collections.unmodifiableList(fortuneDatas);
  }

  public SolarTerms getPrevMajorSolarTerms() {
    return prevMajorSolarTerms;
  }

  public SolarTerms getNextMajorSolarTerms() {
    return nextMajorSolarTerms;
  }

  public Location getLocation() {
    return location;
  }

  public String getLocationName() {
    return locationName;
  }

  /** 取得命宮 (上升星座) */
  public StemBranch getRisingStemBranch() {
    return risingStemBranch;
  }

  /** 太陽 */
  public Branch getSunBranch() {
    return sunBranch;
  }

  /** 月亮 */
  public Branch getMoonBranch() {
    return moonBranch;
  }

  public boolean isDst() {
    return dst;
  }
}
