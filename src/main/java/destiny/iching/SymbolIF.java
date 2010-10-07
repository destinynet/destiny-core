/**
 * @author smallufo
 * @date 2002/8/13
 * @time 下午 05:38:34
 */
package destiny.iching;

import destiny.core.chinese.YinYang;

/**
 * Symbol(八卦) 的介面 , 主要 methods : getName() , 及 getYinYang(0<=index<=2)
 */
public interface SymbolIF
{
  /**
   * 傳回八卦名
   */
  public char getName();
  
  /**
   * 取得一個卦的第幾爻
   * index 值為 0,1 or 2
   */
  public YinYang getYinYang(int index);
}
