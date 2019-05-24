/**
 * @author smallufo
 * @date 2002/9/25
 * @time 上午 02:34:36
 */
package destiny.fengshui.sanyuan

import destiny.iching.Symbol

import java.io.Serializable

/**
 * 在三元盤中，每個 Chart 都有九個 Block
 * 此 Class 即存放每個 Block 的資料
 */
data class ChartBlock(

  /** 方向 , 以卦來表示,  如果為 null , 代表是中宮 */
  val symbol: Symbol?,

  /** 山盤 , 1~9 */
  val mnt: Int,

  /** 向盤 , 1~9 */
  val dir: Int,

  /** 元運 , 1~9 */
  val period: Int) : Serializable