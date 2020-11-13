/**
 * @author smallufo
 * Created on 2007/3/10 at 上午 4:50:49
 */
package destiny.oracles.chuge

import java.io.Serializable

interface IChugeLine : Serializable {
  /** 籤詩  */
  val content: String
  /** 解籤  */
  val explanation: String
}

/**
 * 諸葛神數的「爻」 , 包含 籤詩 以及 解籤
 */
data class ChugeLine(private val yinyang: Boolean,
                     /** 籤詩  */
                     override val content: String,
                     /** 解籤  */
                     override val explanation: String) : IChugeLine

