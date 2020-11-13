/**
 * Created by smallufo on 2020-11-10.
 */
package destiny.oracles.phrom

import destiny.oracles.Explanation
import java.io.Serializable

/** 四面佛三十籤 */
data class Phrom(

  /** 第幾籤 (1~30) */
  val index: Int,

  val level: Level,

  val poems: List<String>,

  val explanations: Set<Explanation>
) : Serializable {

  enum class Level { 上, 中, 下 }
}
