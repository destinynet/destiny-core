/**
 * @author smallufo
 * Created on 2008/6/19 at 上午 1:48:49
 */
package destiny.astrology

import destiny.core.Descriptive

/** 計算一張命盤 ( Horoscope ) 中，的交角列表  */
interface IHoroscopeAspectsCalculator : Descriptive {

  /** 取得與 [Point] 形成交角的星體，以及其交角是哪種 ，如果沒形成任何交角，傳回 null  */
  fun getPointAspect(point: Point, horoscope: IHoro, points: Collection<Point>): Map<Point, Aspect>
}
