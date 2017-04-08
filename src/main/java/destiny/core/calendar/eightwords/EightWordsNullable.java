/**
 * Created by smallufo on 2014-08-03.
 */
package destiny.core.calendar.eightwords;

import destiny.core.chinese.Branch;
import destiny.core.chinese.Stem;
import destiny.core.chinese.StemBranchOptional;
import destiny.tools.ChineseStringTools;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 八字資料結構，可以包含 null 值
 */
public class EightWordsNullable implements Serializable {

  @NotNull
  StemBranchOptional year;

  @NotNull
  StemBranchOptional month;

  @NotNull
  StemBranchOptional day;

  @NotNull
  StemBranchOptional hour;

  public EightWordsNullable(@NotNull StemBranchOptional year,
                            @NotNull StemBranchOptional month,
                            @NotNull StemBranchOptional day,
                            @NotNull StemBranchOptional hour) {
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
      + hour .getStemOptional().map(Stem::toString).orElse(ChineseStringTools.NULL_CHAR)
      + day  .getStemOptional().map(Stem::toString).orElse(ChineseStringTools.NULL_CHAR)
      + month.getStemOptional().map(Stem::toString).orElse(ChineseStringTools.NULL_CHAR)
      + year .getStemOptional().map(Stem::toString).orElse(ChineseStringTools.NULL_CHAR)
      + "\n"
      + hour .getBranchOptional().map(Branch::toString).orElse(ChineseStringTools.NULL_CHAR)
      + day  .getBranchOptional().map(Branch::toString).orElse(ChineseStringTools.NULL_CHAR)
      + month.getBranchOptional().map(Branch::toString).orElse(ChineseStringTools.NULL_CHAR)
      + year .getBranchOptional().map(Branch::toString).orElse(ChineseStringTools.NULL_CHAR)
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

  /**
   * 做法：將 年干、年支、月干、月支、日干、日支、時干、時支 分別取 index (1-based) , 若為 null 則取 0 , 再合併成一個 list
   */
  private static List<Integer> getIntList(EightWordsNullable ewn) {
    List<Integer> list = new ArrayList<>();
    list.add(ewn.getYear() .getStemOptional()  .map(Stem  ::getIndexFromOne).orElse(0));
    list.add(ewn.getYear() .getBranchOptional().map(Branch::getIndexFromOne).orElse(0));
    list.add(ewn.getMonth().getStemOptional()  .map(Stem  ::getIndexFromOne).orElse(0));
    list.add(ewn.getMonth().getBranchOptional().map(Branch::getIndexFromOne).orElse(0));
    list.add(ewn.getDay()  .getStemOptional()  .map(Stem  ::getIndexFromOne).orElse(0));
    list.add(ewn.getDay()  .getBranchOptional().map(Branch::getIndexFromOne).orElse(0));
    list.add(ewn.getHour() .getStemOptional()  .map(Stem  ::getIndexFromOne).orElse(0));
    list.add(ewn.getHour() .getBranchOptional().map(Branch::getIndexFromOne).orElse(0));
    return list;
  }

  public List<Integer> getIntList() {
    return getIntList(this);
  }

  /**
   * 從 list of integer (1-based) 轉換成 EightWordsNullable
   * TODO : 這要如何能夠自動 downcast 成 EightWords 呢？
   * <T extends EightWordsNullable> T 好像不行 , return new 不知要寫什麼？
   */
  public static EightWordsNullable getFromIntList(List<Integer> list) {
    assert list != null && list.size() == 8;
    Optional<Stem>   yearStem    = list.get(0) == 0 ? Optional.empty() : Optional.of(Stem.get  (list.get(0)-1));
    Optional<Branch> yearBranch  = list.get(1) == 0 ? Optional.empty() : Optional.of(Branch.get(list.get(1)-1));
    Optional<Stem>   monthStem   = list.get(2) == 0 ? Optional.empty() : Optional.of(Stem.get  (list.get(2)-1));
    Optional<Branch> monthBranch = list.get(3) == 0 ? Optional.empty() : Optional.of(Branch.get(list.get(3)-1));
    Optional<Stem>   dayStem     = list.get(4) == 0 ? Optional.empty() : Optional.of(Stem.get  (list.get(4)-1));
    Optional<Branch> dayBranch   = list.get(5) == 0 ? Optional.empty() : Optional.of(Branch.get(list.get(5)-1));
    Optional<Stem>   hourStem    = list.get(6) == 0 ? Optional.empty() : Optional.of(Stem.get  (list.get(6)-1));
    Optional<Branch> hourBranch  = list.get(7) == 0 ? Optional.empty() : Optional.of(Branch.get(list.get(7)-1));
    return new EightWordsNullable(
      StemBranchOptional.get(yearStem , yearBranch) ,
      StemBranchOptional.get(monthStem , monthBranch) ,
      StemBranchOptional.get(dayStem , dayBranch),
      StemBranchOptional.get(hourStem , hourBranch)
    );
  }
}
