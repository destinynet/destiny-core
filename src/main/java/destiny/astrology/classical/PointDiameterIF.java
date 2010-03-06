/**
 * @author smallufo 
 * Created on 2007/11/25 at 上午 12:16:57
 */ 
package destiny.astrology.classical;

import destiny.astrology.Point;
import destiny.core.Descriptive;

/**
 * 「古典占星術」所使用，取得星體光芒的「直徑」
 * 內定實作為 PointDiameterAlBiruniImpl , 未來可以用資料庫實作。
 */
public interface PointDiameterIF extends Descriptive
{
  /** 如果不是行星，則光芒直徑一律視為 2.0 */
  public double getDiameter(Point point);
}
