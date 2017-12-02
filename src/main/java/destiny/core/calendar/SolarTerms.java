package destiny.core.calendar;


import destiny.core.chinese.Branch;
import destiny.tools.ArrayTools;

import java.util.Arrays;

import static destiny.core.chinese.Branch.寅;

public enum SolarTerms {
  立春("立春",315),
  雨水("雨水",330),
  驚蟄("驚蟄",345),
  春分("春分",  0),
  清明("清明", 15),
  穀雨("穀雨", 30),
  立夏("立夏", 45),
  小滿("小滿", 60),
  芒種("芒種", 75),
  夏至("夏至", 90),
  小暑("小暑",105),
  大暑("大暑",120),
  立秋("立秋",135),
  處暑("處暑",150),
  白露("白露",165),
  秋分("秋分",180),
  寒露("寒露",195),
  霜降("霜降",210),
  立冬("立冬",225),
  小雪("小雪",240),
  大雪("大雪",255),
  冬至("冬至",270),
  小寒("小寒",285),
  大寒("大寒",300);

  private final String name;
  private final int zodiacDegree;

  private final static SolarTerms[] VALUES =
    { 立春 , 雨水 , 驚蟄 , 春分 , 清明 , 穀雨,
      立夏 , 小滿 , 芒種 , 夏至 , 小暑 , 大暑 ,
      立秋 , 處暑 , 白露 , 秋分 , 寒露 , 霜降 ,
      立冬 , 小雪 , 大雪 , 冬至 , 小寒 , 大寒
    };

  SolarTerms(String name, int zodiacDegree) {
    this.name = name;
    this.zodiacDegree = zodiacDegree;
  }

  /**
   * @param solarTerm 節氣
   * @return 傳回 index , 立春為 0 , 雨水為 1 , ... , 大寒 為 23
   */
  public static int getIndex(SolarTerms solarTerm) {
    return Arrays.binarySearch(VALUES , solarTerm);
  }

  public SolarTerms next() {
    return get(SolarTerms.getIndex(this)+1);
  }

  public SolarTerms previous() {
    return get(SolarTerms.getIndex(this)-1);
  }

  /** 取得節氣的名稱 */
  public String getName() { return name; }

  public int getZodiacDegree() { return zodiacDegree; }


  public String toString() {
    return name;
  }

  /**
   * @param solarTermsIndex 節氣的索引
   * @return 0 傳回立春 , 1 傳回 雨水 , ... , 23 傳回 大寒 , 接著連續 24 傳回立春
   */
  public static SolarTerms get(int solarTermsIndex) {
    return ArrayTools.INSTANCE.get(VALUES , solarTermsIndex);
  }

  /**
   * @return 從黃經度數，取得節氣
   */
  public static SolarTerms getFromDegree(double degree) {
    int index = (int)degree/15 +3;
    if (index >= 24)
      index = index - 24;
    return get(index);
  }

  /**
   * 此「節氣」是否是「節」
   * 立春 => true
   * 雨水 => false
   * 驚蟄 => true
   * ...
   */
  public boolean isMajor() {
    return SolarTerms.getIndex(this) % 2 == 0;
  }

  /** 取得地支 */
  public Branch getBranch() {
    int index = getIndex(this);
    return 寅.next(index / 2);
  }

}
