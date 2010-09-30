/** 2009/6/23 上午 1:56:39 by smallufo */
package destiny.IChing;

import destiny.core.chinese.YinYangIF;

/** 一個易經六十四卦的 Container */
public interface IChingIF
{
  /** 取得六十四個卦，『不保證卦序』 */
  public HexagramIF[] getHexagrams();
  
  /** 從六個陰陽介面，取得一個卦 */
  public HexagramIF getHexagram(YinYangIF[] yinyangs);
  
  /** 從六個 boolean 值 , 取得一個卦*/
  public HexagramIF getHexagram(boolean[] lineBoolean);
}

