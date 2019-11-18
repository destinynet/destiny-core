/**
 * @author smallufo
 * @date 2002/9/23
 * @time 下午 03:02:51
 */
package destiny.fengshui

import java.io.Serializable

interface ICompass<T> : Serializable {

  /**
   * 取得某個此輪初始元素的起始度數
   */
  val initDegree: Double

  /**
   * 取得此輪每個元素的間隔度數
   */
  val stepDegree: Double

  /**
   * 取得某個元素的起始度數
   */
  fun getStartDegree(t : T): Double


  /**
   * 取得某個元素的結束度數
   */
  fun getEndDegree(t : T): Double

  /**
   * 此度數 是什麼元素
   */
  fun get(degree: Double) : T
}
