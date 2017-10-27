/**
 * Created by smallufo on 2017-04-25.
 */
package destiny.core.calendar.eightwords;

import destiny.core.calendar.Location;
import destiny.core.calendar.SolarTerms;
import destiny.core.calendar.TimeTools;
import destiny.core.calendar.chinese.ChineseDate;
import destiny.core.chinese.Branch;
import destiny.core.chinese.StemBranch;
import org.jooq.lambda.tuple.Tuple2;

import java.io.Serializable;
import java.time.chrono.ChronoLocalDateTime;

/**
 * 一個八字命盤「額外」的計算結果 , 方便排盤輸出
 * Note : 仍然不包含「人」的資訊（性別、大運、歲數...等）
 */
public class EightWordsContextModel implements Serializable {

  protected final EightWords eightWords;

  protected final ChronoLocalDateTime lmt;

  protected final Location location;

  /** 地點名稱 */
  private final String place;

  private final int gmtMinuteOffset;

  /** 日光節約 */
  private final boolean dst;

  /** 農曆 */
  protected final ChineseDate chineseDate;

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


  public EightWordsContextModel(EightWords eightWords, ChronoLocalDateTime lmt, Location location, String place, ChineseDate chineseDate, SolarTerms prevMajorSolarTerms, SolarTerms nextMajorSolarTerms, StemBranch risingStemBranch, Branch sunBranch, Branch moonBranch) {
    this.eightWords = eightWords;
    this.lmt = lmt;
    this.location = location;
    this.place = place;
    Tuple2<Boolean, Integer> tuple = TimeTools.getDstSecondOffset(lmt , location);
    this.dst = tuple.v1();
    this.gmtMinuteOffset = tuple.v2() / 60;
    this.chineseDate = chineseDate;
    this.prevMajorSolarTerms = prevMajorSolarTerms;
    this.nextMajorSolarTerms = nextMajorSolarTerms;
    this.risingStemBranch = risingStemBranch;
    this.sunBranch = sunBranch;
    this.moonBranch = moonBranch;
  }

  public EightWords getEightWords() {
    return eightWords;
  }

  public ChronoLocalDateTime getLmt() {
    return lmt;
  }

  public Location getLocation() {
    return location;
  }

  public String getPlace() {
    return place;
  }

  public int getGmtMinuteOffset() {
    return gmtMinuteOffset;
  }

  public boolean isDst() {
    return dst;
  }

  public ChineseDate getChineseDate() {
    return chineseDate;
  }

  public SolarTerms getPrevMajorSolarTerms() {
    return prevMajorSolarTerms;
  }

  public SolarTerms getNextMajorSolarTerms() {
    return nextMajorSolarTerms;
  }

  public StemBranch getRisingStemBranch() {
    return risingStemBranch;
  }

  public Branch getSunBranch() {
    return sunBranch;
  }

  public Branch getMoonBranch() {
    return moonBranch;
  }
}
