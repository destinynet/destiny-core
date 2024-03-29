/**
 * @author smallufo
 * @date 2002/9/25
 * @time 上午 02:34:36
 */
package destiny.core.fengshui.sanyuan

import destiny.core.fengshui.sanyuan.Period.Companion.toPeriod
import destiny.core.iching.Symbol

import java.io.Serializable

/**
 * 在三元盤中，每個 Chart 都有九個 Block
 * 此 Class 即存放每個 Block 的資料
 */
data class ChartBlock(

  /** 方向 , 以卦來表示,  如果為 null , 代表是中宮 */
  val symbol: Symbol?,

  /** 山盤 , 1~9 */
  val mnt: Period,

  /** 向盤 , 1~9 */
  val dir: Period,

  /** 元運 , 1~9 */
  val period: Period) : Serializable {

    companion object {
      fun of(symbol: Symbol? , mnt: Int , dir: Int , period: Int) : ChartBlock {
        return ChartBlock(symbol, mnt.toPeriod(), dir.toPeriod(), period.toPeriod())
      }
    }
  }
