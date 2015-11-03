/** 2009/6/18 by smallufo */
package destiny.iching;

import destiny.core.Descriptive;

import java.util.Collection;
import java.util.Locale;


/** 對一個「卦」的註釋 */
public interface HexagramContentProvider extends Descriptive
{
  /** 支援哪種(些)語言 */
  Collection<Locale> getLocales();
  
  /** 取得此卦的解釋 , 如果沒有，則傳回 null */
  String getValue(HexagramIF hexagram , Locale locale);
  
  /** 
   * @param lineIndex 1<=lineIndex<=6 
   * @return 取得第幾爻的解釋 , 如果沒有，則傳回 null
   */
  String getLineValue(HexagramIF hexagram , int lineIndex , Locale locale);
  
  /** 額外的註解（ex : 用九、用六） , 如果沒有，則傳回 null */
  String getExtraExpression(HexagramIF hexagram , Locale locale );

}
