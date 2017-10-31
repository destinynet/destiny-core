/**
 * @author smallufo 
 * Created on 2008/6/19 at 上午 1:48:49
 */ 
package destiny.astrology;

import destiny.core.Descriptive;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;

/** 計算一張命盤 ( Horoscope ) 中，的交角列表 */
public interface IHoroscopeAspectsCalculator extends Descriptive {

  /** 設定星盤 */
  void setHoroscope(Horoscope horoscope);
  
  /** 取得與 {@link Point} 形成交角的星體，以及其交角是哪種 ，如果沒形成任何交角，傳回 null */
  @NotNull
  Map<Point , Aspect> getPointAspect(Point point, Collection<Point> points);
}
