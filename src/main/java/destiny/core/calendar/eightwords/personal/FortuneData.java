/**
 * Created by smallufo on 2014-11-30.
 */
package destiny.core.calendar.eightwords.personal;

import destiny.core.chinese.StemBranch;

import java.io.Serializable;

/** 一柱大運的相關資訊。包含上方文字，干支... */
public class FortuneData implements Serializable {

  /** 大運干支 */
  private final StemBranch stemBranch;

  /** 起運時刻 */
  private final double startFortuneGmtJulDay;

  /** 終運時刻 */
  private final double endFortuneGmtJulDay;

  /** 起運歲數 (可能是虛歲、或是實歲) */
  private final int startFortuneAge;

  /** 終運歲數 (可能是虛歲、或是實歲) */
  private final int endFortuneAge;

  FortuneData(StemBranch stemBranch, double startFortuneGmtJulDay, double endFortuneGmtJulDay, int startFortuneAge, int endFortuneAge) {
    this.stemBranch = stemBranch;
    this.startFortuneGmtJulDay = startFortuneGmtJulDay;
    this.endFortuneGmtJulDay = endFortuneGmtJulDay;
    this.startFortuneAge = startFortuneAge;
    this.endFortuneAge = endFortuneAge;
  }

  @Override
  public String toString() {
    return "{" + startFortuneAge + " " + stemBranch +'}';
  }

  public StemBranch getStemBranch() {
    return stemBranch;
  }

  public int getStartFortuneAge() {
    return startFortuneAge;
  }

  public int getEndFortuneAge() {
    return endFortuneAge;
  }

  public double getStartFortuneGmtJulDay() {
    return startFortuneGmtJulDay;
  }

  public double getEndFortuneGmtJulDay() {
    return endFortuneGmtJulDay;
  }
} // FortuneData
