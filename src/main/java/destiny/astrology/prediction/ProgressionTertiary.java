package destiny.astrology.prediction;

import destiny.astrology.Constants;

/** 
 * Tertiary Progressions 一日一月法 , 參限法
 * <pre>
 * Puka : 此方法為德國占星家 E.H. Troinski所創，由於地球上最明顯的星體運動為日夜遞轉（地球自轉，產生主限法的觀念）、
 * 月亮陰晴圓缺（月對地公轉）、太陽四季的往復（地對太陽的公轉，產生次限法的觀念），
 * 故占星家取地球自轉的改變而形成主限法，取自轉一周等於太陽公轉一周而成次限法，
 * 地球自轉一周等於月亮公轉一周而成為參限法。換言之，即出生後一日等於一個太陰月。
 * 此方法因可用的行星較次限法多，故有占星家認為較次限為佳而推薦使用（如美國占星家Noel Tyl）。
 * 
 * 參限法可用來查看較短期的運勢（月、週），但就整個占星學界而言，參限法的使用並不如次限法或流年法來的普遍。
 * 參限法的應用觀念同於次限法，差別只在考量參限星運行速度產生的時效長短與次限法不同。
 * 例如：次限月亮每月約行一度，而參限月亮約一周行3.5～4度。
 * </pre> 
 */
public class ProgressionTertiary extends AbstractProgression
{
  /** 一月有幾秒 */
  private double monthSeconds = Constants.SIDEREAL_MONTH;
  
  /** 一日有幾秒 */
  private double daySeconds = Constants.SIDEREAL_DAY;
  
  /** TP (一日一月) , 分子是 一月 */
  @Override
  protected double getNumerator()
  {
    return this.monthSeconds;
  }
  
  /** TP (一日一月) , 分母是 一日 */
  @Override
  protected double getDenominator()
  {
    return this.daySeconds;
  }

  public double getMonthSeconds()
  {
    return monthSeconds;
  }

  public void setMonthSeconds(double monthSeconds)
  {
    this.monthSeconds = monthSeconds;
  }

  public double getDaySeconds()
  {
    return daySeconds;
  }

  public void setDaySeconds(double daySeconds)
  {
    this.daySeconds = daySeconds;
  }
  
}