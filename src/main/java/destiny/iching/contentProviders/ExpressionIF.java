/**
 * @author smallufo
 * Created on 2010/10/25 at 下午9:23:08
 */
package destiny.iching.contentProviders;

import java.util.Locale;

import destiny.iching.HexagramIF;

public interface ExpressionIF
{
  /** 取得卦辭 */
  public String getHexagramExpression(HexagramIF hexagram , Locale locale);
  
  /**
   * @param hexagram
   * @param lineIndex 0~7 (0 的話則為針對卦辭)
   * @param locale
   * @return 取得爻辭
   */
  public String getLineExpression(HexagramIF hexagram , int lineIndex , Locale locale);
  
  /** 取得 用六/用九 的爻辭 */
  public String getExtraExpression(HexagramIF hexagram , Locale locale);
}
