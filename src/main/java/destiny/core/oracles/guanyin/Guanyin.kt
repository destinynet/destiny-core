/**
 * Created by smallufo on 2020-11-16.
 */
package destiny.core.oracles.guanyin

import destiny.core.oracles.Verdict
import java.io.Serializable


data class Guanyin(
  val index: Int,

  val level: String?,

  val stories: Set<String>,

  /** 七言絕句 , 四句話 */
  val poem: List<String>,

  /** 四句淺釋  */
  val interpretation: String,

  /** 解 */
  val meaning: String,

  /** 勸告 */
  val advice: String?,

  /** 詳解 */
  val detail: String,

  /** 斷語 */
  val verdicts: Set<Verdict>
) : Serializable
