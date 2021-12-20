/**
 * Created by smallufo on 2021-12-21.
 */
package destiny.core.oracles.dizang

import destiny.core.chinese.StemBranch
import kotlinx.serialization.Serializable


@Serializable
data class Dizang(/** 六十甲子 */
                  val stemBranch: StemBranch ,
                  /** 七言絕句 , 四句話 */
                  val poems: List<String>,
                  val scene : String,
                  val shortDescs : List<String> ,
                  val fullDescs : List<String>) : java.io.Serializable
