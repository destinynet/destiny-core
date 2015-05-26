/**
 * Created by smallufo on 2014-08-03.
 */
package destiny.core.calendar.eightwords;

import destiny.core.chinese.Branch;
import destiny.core.chinese.Stem;
import destiny.core.chinese.StemBranchOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 八字資料結構，可以包含 null 值
 */
public class EightWordsNullable implements Serializable {

  @NotNull
  protected StemBranchOptional year;

  @NotNull
  protected StemBranchOptional month;

  @NotNull
  protected StemBranchOptional day;

  @NotNull
  protected StemBranchOptional hour;

  @NotNull
  private final static String NULL_CHAR ="　"; //空白字元，使用全形的空白, 在 toString() 時使用

  public EightWordsNullable(@NotNull StemBranchOptional year, @NotNull StemBranchOptional month, @NotNull StemBranchOptional day, @NotNull StemBranchOptional hour) {
    this.year = year;
    this.month = month;
    this.day = day;
    this.hour = hour;
  }

  public EightWordsNullable() {
    this.year  = StemBranchOptional.empty();
    this.month = StemBranchOptional.empty();
    this.day   = StemBranchOptional.empty();
    this.hour  = StemBranchOptional.empty();
  }

  @NotNull
  public StemBranchOptional getYear() {
    return year;
  }

  @NotNull
  public StemBranchOptional getMonth() {
    return month;
  }

  @NotNull
  public StemBranchOptional getDay() {
    return day;
  }

  @NotNull
  public StemBranchOptional getHour() {
    return hour;
  }

  @Nullable
  public Stem getYearStem()  { return year.getStemOptional().orElse(null);  }
  @Nullable
  public Stem getMonthStem() { return month.getStemOptional().orElse(null); }
  @Nullable
  public Stem getDayStem()   { return day.getStemOptional().orElse(null);  }
  @Nullable
  public Stem getHourStem()  { return hour.getStemOptional().orElse(null);  }

  @Nullable
  public Branch getYearBranch()  { return year.getBranchOptional().orElse(null);  }
  @Nullable
  public Branch getMonthBranch() { return month.getBranchOptional().orElse(null); }
  @Nullable
  public Branch getDayBranch()   { return day.getBranchOptional().orElse(null);   }
  @Nullable
  public Branch getHourBranch()  { return hour.getBranchOptional().orElse(null); }

  /** 取得四柱 */
  @NotNull
  public List<StemBranchOptional> getStemBranches() {
    return new ArrayList<StemBranchOptional>() {{
      add(year);
      add(month);
      add(day);
      add(hour);
    }};
  }


  @Override
  public String toString() {
    return "\n"
      + hour .getStemOptional().map(h -> h.toString()).orElse(NULL_CHAR)
      + day  .getStemOptional().map(h -> h.toString()).orElse(NULL_CHAR)
      + month.getStemOptional().map(h -> h.toString()).orElse(NULL_CHAR)
      + year .getStemOptional().map(h -> h.toString()).orElse(NULL_CHAR)
      + "\n"
      + hour .getBranchOptional().map(b -> b.toString()).orElse(NULL_CHAR)
      + day  .getBranchOptional().map(b -> b.toString()).orElse(NULL_CHAR)
      + month.getBranchOptional().map(b -> b.toString()).orElse(NULL_CHAR)
      + year .getBranchOptional().map(b -> b.toString()).orElse(NULL_CHAR)
      ;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof EightWordsNullable)) {
      return false;
    }

    EightWordsNullable that = (EightWordsNullable) o;

    if (!day.equals(that.day)) {
      return false;
    }
    if (!hour.equals(that.hour)) {
      return false;
    }
    if (!month.equals(that.month)) {
      return false;
    }
    if (!year.equals(that.year)) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = year.hashCode();
    result = 31 * result + month.hashCode();
    result = 31 * result + day.hashCode();
    result = 31 * result + hour.hashCode();
    return result;
  }

  public void setYear(@NotNull StemBranchOptional year) {
    this.year = year;
  }

  public void setMonth(@NotNull StemBranchOptional month) {
    this.month = month;
  }

  public void setDay(@NotNull StemBranchOptional day) {
    this.day = day;
  }

  public void setHour(@NotNull StemBranchOptional hour) {
    this.hour = hour;
  }
}
