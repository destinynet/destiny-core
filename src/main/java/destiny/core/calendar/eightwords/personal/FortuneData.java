/**
 * Created by smallufo on 2014-11-30.
 */
package destiny.core.calendar.eightwords.personal;

import destiny.core.chinese.StemBranch;

import java.io.Serializable;
import java.time.chrono.ChronoLocalDateTime;

/** 一柱大運的相關資訊。包含上方文字，干支... */
public class FortuneData implements Serializable {

  /** 大運干支 */
  private final StemBranch stemBranch;

  /** 起運時刻 */
  private final ChronoLocalDateTime startFortuneLmt;

  /** 終運時刻 */
  private final ChronoLocalDateTime endFortuneLmt;

  /** 起運 : 西元/民國/實歲/虛歲之值 */
  private final int startFortune;

  /** 終運 : 西元/民國/實歲/虛歲之值 */
  private final int endFortune;

  FortuneData(StemBranch stemBranch,
              ChronoLocalDateTime startFortuneLmt,
              ChronoLocalDateTime endFortuneLmt,
              int startFortune, int endFortune) {
    this.stemBranch = stemBranch;
    this.startFortuneLmt = startFortuneLmt;
    this.endFortuneLmt = endFortuneLmt;
    this.startFortune = startFortune;
    this.endFortune = endFortune;
  }

  @Override
  public String toString() {
    return "{" + startFortune + " " + stemBranch +'}';
  }

  public StemBranch getStemBranch() {
    return stemBranch;
  }

  public ChronoLocalDateTime getStartFortuneLmt() {
    return startFortuneLmt;
  }

  public ChronoLocalDateTime getEndFortuneLmt() {
    return endFortuneLmt;
  }

  public int getStartFortune() {
    return startFortune;
  }

  public int getEndFortune() {
    return endFortune;
  }
} // FortuneData
