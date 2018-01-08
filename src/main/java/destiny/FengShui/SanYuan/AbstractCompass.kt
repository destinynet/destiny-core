/**
 * @author smallufo
 * @date 2002/9/23
 * @time 下午 03:02:51
 */
package destiny.FengShui.SanYuan

import java.io.Serializable

abstract class AbstractCompass : Serializable {

  /**
   * 取得某個此輪初始元素的起始度數
   */
  abstract val initDegree: Double

  /**
   * 取得此輪每個元素的間隔度數
   */
  abstract val stepDegree: Double

  /**
   * 取得某個元素的起始度數
   */
  abstract fun getStartDegree(o: Any): Double

  /**
   * 取得某個元素的結束度數
   */
  abstract fun getEndDegree(o: Any): Double

}
