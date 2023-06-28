/**
 * Created by smallufo on 2019-05-02.
 */
package destiny.core.calendar.eightwords

/**
 * 整合「日」與「時」的實作
 */
interface IDayHour : IDay, IHour {

  val hourImpl: IHour
}
