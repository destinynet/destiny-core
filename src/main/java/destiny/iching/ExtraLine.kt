/**
 * @author smallufo
 * @date 2003/1/13
 * @time 下午 05:01:39
 */
package destiny.iching

import java.io.Serializable

/**
 * 用九用六，只有 乾、坤 兩卦才有
 */
data class ExtraLine(
  /** 爻辭  */
  val lineExpression: String,
  /** 爻象  */
  val lineImage: String) : Serializable
