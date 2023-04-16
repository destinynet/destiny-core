/**
 * Created by smallufo on 2020-11-23.
 */
package destiny.core.oracles.storm

import destiny.core.chinese.Stem
import destiny.core.oracles.IClause
import destiny.core.oracles.Verdict
import java.util.*


data class Storm(val index: Int,
                 /** 天干x2 , 長度固定為 2 */
                 val stems: List<Stem>,
                 val level: String,
                 /** 七言絕句 , 四句話 */
                 val poem: List<String>,
                 /** 釋義 */
                 val poemExplain: String,
                 /** 聖意 , 三字一組，共八組，24 字 */
                 val edicts: List<String>,
                 /** 解曰 */
                 val meaning: String,
                 /** 解籤 , advice */
                 val advice: String,
                 /** 故事 */
                 val stories: Set<String>,
                 /** 東坡解 , 八句話 , 每句 4 字 */
                 val dongPo: List<String>,
                 /** 碧仙註 , 五言絕句 or 七言絕句 */
                 val greenNote: List<String>,
                 /** 斷曰 */
                 val verdicts: Set<Verdict>) : IClause {

  override fun getTitle(locale: Locale): String {
    return "雷雨師籤"
  }

  init {
    require(stems.size == 2)
    require(poem.size == 4)
    require(edicts.size == 8)
    require(dongPo.size == 8)
    require(greenNote.size == 4)

    require(!meaning.contains(' '))
    require(!advice.contains(' '))

  }
}
