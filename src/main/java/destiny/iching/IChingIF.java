/** 2009/6/23 上午 1:56:39 by smallufo */
package destiny.iching;

import destiny.core.chinese.IYinYang;
import org.jetbrains.annotations.NotNull;

/** 一個易經六十四卦的 Container */
public interface IChingIF {

  /** 取得六十四個卦，『不保證卦序』 */
  @NotNull
  HexagramIF[] getHexagrams();

  /** 從六個陰陽介面，取得一個卦 */
  @NotNull
  HexagramIF getHexagram(IYinYang[] yinyangs);

  /** 從六個 boolean 值 , 取得一個卦 */
  @NotNull
  HexagramIF getHexagram(boolean[] lineBoolean);
}

