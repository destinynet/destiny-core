/**
 * Created by smallufo on 2015-05-31.
 */
package destiny.core.chinese.liuren.golden;

import destiny.astrology.DayNight;
import destiny.astrology.DayNightDifferentiator;
import destiny.core.Gender;
import destiny.core.calendar.LocationWithName;
import destiny.core.calendar.eightwords.EightWords;
import destiny.core.chinese.ClockwiseIF;
import destiny.core.chinese.MonthMasterIF;
import destiny.core.chinese.StemBranch;
import destiny.core.chinese.TianyiIF;
import destiny.core.chinese.liuren.General;
import destiny.core.chinese.liuren.GeneralSeqIF;
import destiny.core.chinese.liuren.GeneralStemBranchIF;

import java.io.Serializable;
import java.util.Locale;

public class PithyWithMeta implements Serializable {

  /** 起課方式 */
  public enum Method {RANDOM , MANUAL}

  private Method method;

  private final Pithy pithy;

  private final Gender gender;

  private final String question;

  private final LocationWithName locationWithName;

  /** 月將 */
  private final MonthMasterIF monthMasterImpl;

  /** 晝夜區分 */
  private final DayNightDifferentiator dayNightImpl;

  /** 天乙貴人 */
  private final TianyiIF tianyiImpl;

  /** 貴神順逆 */
  private final ClockwiseIF clockwiseImpl;

  /** 12天將順序 */
  private final GeneralSeqIF seqImpl;

  /** 12天將干支 */
  private final GeneralStemBranchIF generalStemBranchImpl;

  public PithyWithMeta(Pithy pithy, Method method , Gender gender, String question, LocationWithName locationWithName, MonthMasterIF monthMasterImpl, DayNightDifferentiator dayNightImpl, TianyiIF tianyiImpl, ClockwiseIF clockwiseImpl, GeneralSeqIF seqImpl, GeneralStemBranchIF generalStemBranchImpl) {
    this.method = method;
    this.pithy = pithy;
    this.gender = gender;
    this.question = question;
    this.locationWithName = locationWithName;
    this.monthMasterImpl = monthMasterImpl;
    this.dayNightImpl = dayNightImpl;
    this.tianyiImpl = tianyiImpl;
    this.clockwiseImpl = clockwiseImpl;
    this.seqImpl = seqImpl;
    this.generalStemBranchImpl = generalStemBranchImpl;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    EightWords ew = pithy.getEightWords();
    sb.append("　日").append("\n");
    sb.append(ew.getHourStem()).append(ew.getDayStem()).append(ew.getMonthStem()).append(ew.getYearStem()).append("\n");
    sb.append(ew.getHourBranch()).append(ew.getDayBranch()).append(ew.getMonthBranch()).append(ew.getYearBranch()).append("\n");
    sb.append("\n");
    sb.append("月將：").append(pithy.getMonthSign()).append("（").append(monthMasterImpl.getTitle(Locale.TAIWAN)).append("）").append("\n");
    sb.append("晝夜：").append(pithy.getDayNight() == DayNight.DAY ? "日" : "夜").append("\n\n");
    sb.append("人元：").append(pithy.getHuman()).append("\n");
    StemBranch 貴神 = pithy.getBenefactor();
    sb.append("貴神：").append(貴神).append("（").append(General.get(貴神.getBranch() , generalStemBranchImpl)).append("）").append("\n");
    StemBranch 將神 = pithy.getJohnson();
    sb.append("將神：").append(將神).append("（").append(MonthMasterIF.getName(將神.getBranch())).append("）").append("\n");
    sb.append("地分：").append(pithy.getDirection());
    sb.append("\n\n");

    sb.append("性別：").append(gender).append("\n");
    sb.append("問題：").append(question).append("\n");
    sb.append("地點：").append(locationWithName.getName()).append("\n");

    if (method != null)
      sb.append("起課方式：").append(method == Method.RANDOM ? "電腦起課" : "手動起課").append("\n");
    sb.append("晝夜設定：").append(dayNightImpl.getTitle(Locale.TAIWAN)).append("\n");
    sb.append("天乙貴人：").append(tianyiImpl.getTitle(Locale.TAIWAN)).append("\n");
    sb.append("順逆設定：").append(clockwiseImpl.getTitle(Locale.TAIWAN)).append("\n");
    sb.append("天將順序：").append(seqImpl.getTitle(Locale.TAIWAN)).append("\n");
    sb.append("天將干支：").append(generalStemBranchImpl.getTitle(Locale.TAIWAN)).append("\n");

    return sb.toString();
  }

  public Pithy getPithy() {
    return pithy;
  }

  public Gender getGender() {
    return gender;
  }

  public String getQuestion() {
    return question;
  }

  public LocationWithName getLocationWithName() {
    return locationWithName;
  }

  public MonthMasterIF getMonthMasterImpl() {
    return monthMasterImpl;
  }

  public DayNightDifferentiator getDayNightImpl() {
    return dayNightImpl;
  }

  public TianyiIF getTianyiImpl() {
    return tianyiImpl;
  }

  public ClockwiseIF getClockwiseImpl() {
    return clockwiseImpl;
  }

  public GeneralSeqIF getSeqImpl() {
    return seqImpl;
  }


}
