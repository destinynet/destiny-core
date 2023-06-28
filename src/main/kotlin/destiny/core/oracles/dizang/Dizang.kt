/**
 * Created by smallufo on 2021-12-21.
 */
package destiny.core.oracles.dizang

import destiny.core.chinese.StemBranch
import destiny.core.oracles.IClause
import kotlinx.serialization.Serializable
import java.util.*


@Serializable
data class Dizang(/** 六十甲子 */
                  val stemBranch: StemBranch,
                  /** 七言絕句 , 四句話 */
                  val poem: List<String>,
                  val scene : String,
                  val shortDescs : List<String>,
                  val fullDescs : List<String>) : IClause {

  override fun getTitle(locale: Locale): String {
    return "地藏靈籤"
  }

}
