package destiny.astrology.prediction;

import destiny.astrology.Constants;

/** 
 * Secondary Progressions 一日一年法 , 次限法
 * <pre>
 * Puka : 由十六世紀時占星家瑪吉尼斯（Antonius Maginus）所創，他根據主限法的概念稍加改變，
 * 以行星在黃道上移動（稱為次要運動Secondary Motion）為主要起盤依據。
 * 次限法的原理是出生後一天等於人生實際一歲，也就是說要推算某人20歲時的運勢，可以觀察某人出生後第20天的星象。
 * 使用次限法時可將次限盤視為一個獨立的星盤，直接觀察次限盤上行星位置與交角，以推測運勢。
 * 亦可以將次限盤置於外圈，內圈則為本命星圖，觀察次限星與本命星的交角。
 * 由於次限星運行甚緩，故一些不常用的次要相位(如30度、150度)亦要觀察。筆者使用的習慣為將次限盤置於外圈，本命星置於內圈。
 * 
 * 次限法中次限三王星運行甚慢，可忽略不看(除非年紀甚大，次限三王星才會移動較多)。
 * 次限土星及次限木星一般而言是一生甚長的趨勢，例如次限木星三合本命太陽，則本命上太陽與木星往往就有120度交角，故重要性不大。
 * 但次限木星、次限土星與本命星的交角越來越緊，或是次限土星、次限木星逆行、順行時必須留意。
 * 
 * 次限火星、次限金星、次限水星、次限太陽影響力從一年至數年不等，可視為一種大運。而詳細的流年可從流年星(Transits)的引動去判斷。
 * 次限太陽大約一年推進一度，而月亮約兩年半推進一個星座。次限月亮的位置必須有詳細的出生時間才加以考慮。
 * 次限月亮每月運行約一度，可視為次限法的引動星，不過次限月亮的吉凶交角不應賦予過多的意義。
 * 另一個不可忽略的是角點(Asc,MC)，包含角點與次限星有交角或是次限命度、次限天頂與本命星或次限星有交角。
 * 角點的接觸具有最高的優先性，通常代表了大的變化，新的方向。
 * 
 * 由於次限星推進很慢，因此交角容許度也較緊。大部分占星家會建議使用一度的容許度。但若是外行星的推進，則應將容許度再縮小至小於一度。
 * 太陽與月亮因對個人的影響較大，可略放寬為一度半的容許度。過大的容許度將使得無法預測較精確的時間點，容易給人矇到的感覺。
 * 追蹤次限月亮的推進，常可發現當它與本命星準確交角時引動了該事件的發生。
 * 在應用上除了查看次限星對本命星形成的相位之外，也有占星家認為應該加看次限星彼此形成的相位關係，
 * 以及流年星（Transits）對次限星所形成的相位關係。
 * </pre>
 */
public class ProgressionSecondary extends AbstractProgression {
  /** 一年有幾秒 */
  private double yearSeconds = Constants.SIDEREAL_YEAR;
  
  /** 一日有幾秒 */
  private double daySeconds = Constants.SIDEREAL_DAY;
  
  /** SP (一日一年) , 分子是 一年 */
  @Override
  protected double getNumerator()
  {
    return this.yearSeconds;
  }
  
  /** SP (一日一年) , 分母是 一日 */
  @Override
  protected double getDenominator()
  {
    return this.daySeconds;
  }

  public double getYearSeconds()
  {
    return yearSeconds;
  }

  public void setYearSeconds(double yearSeconds)
  {
    this.yearSeconds = yearSeconds;
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