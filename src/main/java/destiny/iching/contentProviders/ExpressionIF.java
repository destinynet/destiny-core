/**
 * @author smallufo
 * Created on 2010/10/25 at 下午9:23:08
 */
package destiny.iching.contentProviders;

import destiny.iching.HexagramIF;

import java.util.Locale;

/**
 * 卦 或 爻 的辭
 */
public interface ExpressionIF
{
  /** 取得卦辭 */
  String getHexagramExpression(HexagramIF hexagram , Locale locale);
  
  /**
   * @param hexagram
   * @param lineIndex 0~7 (0 的話則為針對卦辭)
   * @param locale
   * @return 取得爻辭
   */
  String getLineExpression(HexagramIF hexagram , int lineIndex , Locale locale);
  
  /** 取得 用六/用九 的爻辭 */
  String getExtraExpression(HexagramIF hexagram , Locale locale);
}
