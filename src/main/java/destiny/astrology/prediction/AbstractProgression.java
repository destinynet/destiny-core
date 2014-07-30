/**
 * @author smallufo 
 * Created on 2008/4/5 改寫
 */
package destiny.astrology.prediction;

import destiny.core.calendar.Time;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * Progression 抽象類別，具備 Progression 演算法的 template methods
 */
public abstract class AbstractProgression implements LinearIF , Conversable , Serializable
{
  /** 是否逆推，內定是順推 */
  private boolean converse = false;

  /** Numerator: 分子 , 假設以 SecondaryProgression (一日一年)來說 , 分子是一年(有幾秒) */
  abstract protected double getNumerator();
  
  /** Denominator: 分母 , 假設以 SecondaryProgression (一日一年)來說 , 分母是一日(有幾秒) */
  abstract protected double getDenominator();
  
  @Override
  public void setConverse(boolean value)
  {
    this.converse = value;
  }
  
  @Override
  public boolean isConverse()
  {
    return converse;
  }

  /** 
   * 實作 Mappable 
   * Template Method , 計算 nowTime 相對於 natalTime , 「收斂(converge)」到的時間<br/>
   * 不限定是 GMT 或是 LMT , 但兩者要一樣的時區 
   */
  @Override
  public Time getConvergentTime(@NotNull Time natalTime , @NotNull Time nowTime)
  {
    double diffSeconds = nowTime.diffSeconds(natalTime);
    Time resultTime;
    if (converse)
      resultTime = new Time(natalTime , - (diffSeconds / getNumerator())*getDenominator());
    else
      resultTime = new Time(natalTime ,   (diffSeconds / getNumerator())*getDenominator());
    return resultTime;
  }
  

  /** 
   * 實作 LinearIF
   * Template Method , 計算從 nowTime 相對於 natalTime , 「發散(diverge)」到(未來的)哪個時間<br/>
   * 不限定是 GMT 或是 LMT , 但兩者要一樣的時區 
   */
  @Override
  public Time getDivergentTime(@NotNull Time natalTime , @NotNull Time nowTime)
  {
    double diffSeconds = nowTime.diffSeconds(natalTime);
    Time resultTime;
    if (converse == true)
      resultTime = new Time(natalTime , - (diffSeconds / getDenominator())*getNumerator());
    else
      resultTime = new Time(natalTime ,   (diffSeconds / getDenominator())*getNumerator());
    return resultTime;
  }
  

}
