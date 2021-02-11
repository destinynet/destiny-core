/**
 * @author smallufo
 * Created on 2008/4/29 at 上午 5:40:13
 */
package destiny.core.oracles.sixty

import destiny.core.chinese.FiveElement
import destiny.core.chinese.StemBranch
import destiny.core.iching.Hexagram
import destiny.core.oracles.Verdict
import java.io.Serializable


/**
 * 六十甲子籤的一條籤詩
 */
data class Sixty(

  /** 頭籤 , 籤首 , or 籤王 ,  */
  val special: String?,

  /** 第幾支籤 , 若為 null 代表 [special] 必定有值 */
  val index: Int?,

  /** 干支 , 若為 null 代表  [special] 必定有值 */
  val stemBranch: StemBranch?,

  /** 所配的卦象陰陽，從下到上 , 若為 null 代表 [special] 必定有值 */
  val hexagram: Hexagram?,

  /**
   * 所屬的五行 : 若為 null 代表「籤王」
   * 屬金利秋宜西方
   * 屬水利冬宜北方
   * 屬木利春宜東方
   * 屬火利夏宜南方
   * 屬土四季四方宜
   */
  val fiveElement: FiveElement?,

  /** 七言絕句 , 四句話 */
  val poems: List<String>,

  /** 故事  */
  val stories: List<String>,

  val verdicts: Set<Verdict> = emptySet()

) : Serializable


/**
 * 透過 籤號 (1~60) 或是 spacial (頭籤 , 籤首 , or 籤王) 來定位出 Clause
 */
data class SixtyMatcher(val indexFrom1: Int?, val special: String?)  {
  init {
    require(indexFrom1 != null || special != null) {
      "籤號 & special 不可同時為 null"
    }
  }
}
