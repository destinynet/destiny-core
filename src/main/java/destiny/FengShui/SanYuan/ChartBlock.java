/**
 * @author smallufo
 * @date 2002/9/25
 * @time 上午 02:34:36
 */
package destiny.FengShui.SanYuan;

import destiny.iching.Symbol;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

/**
 * 在三元盤中，每個 Chart 都有九個 Block
 * 此 Class 即存放每個 Block 的資料
 */
public class ChartBlock implements Serializable
{
  /**
   * 方向 , 以卦來表示
   * 如果為 null , 代表是中宮
   */
  @Nullable
  private Symbol symbol;
  
  /**
   * 山盤 , 1~9
   */
  private int mountain;
  
  /**
   * 向盤 , 1~9
   */
  private int direction;
  
  /**
   * 元運 , 一 ~ 九
   */
  private int period;

  /**
   * constructor
   */
  public ChartBlock(int period)
  {
    this.period = period;
  }
  
  public ChartBlock(Symbol symbol , int mountain , int direction , int period)
  {
    this.symbol = symbol ;
    this.mountain = mountain;
    this.direction = direction;
    this.period = period;
  }
  
  @Nullable
  public Symbol getSymbol() { return symbol; }
  public void setSymbol(Symbol s) 
  { 
    symbol = s;
  }
  
  /**
   * 山盤 , 1~9
   */
  public int getMountain() { return mountain; }
  public void setMountain(int mountain)
  {
    this.mountain = mountain;
  }
  
  /**
   * 向盤 , 1~9
   */
  public int getDirection() { return direction; }
  public void setDirection(int direction)
  {
    this.direction = direction;
  }
  
  /**
   * 元運 , 1~9
   */
  public int getPeriod() { return period; }

  @NotNull
  @Override
  public String toString()
  {
    return "ChartBlock ["+symbol+"方,"+period+"運,"+mountain+"山"+direction+"向]";
  }
  
}
