/**
 * @author smallufo
 * Created on 2008/7/26 at 上午 2:29:14
 */
package destiny.core.calendar

/** 具備 年 , 月 , 日 的介面  */
interface IDate {

  /** 是否西元後  */
  val isAd: Boolean

  val year: Int

  val month: Int

  val day: Int
}
