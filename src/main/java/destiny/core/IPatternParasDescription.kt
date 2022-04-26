package destiny.core

import java.io.Serializable
import java.util.*

/**
 * 整合 [IPattern] , 以及 [Descriptive]
 * 另外新增 list of [Paragraph] 作為段落解說
 */
interface IPatternParasDescription : IPattern, Descriptive {
  val pattern: IPattern
  val paras: List<Paragraph>
}


data class PatternParasDescription(
  override val pattern: IPattern,
  override val paras: List<Paragraph>
) : Serializable, IPatternParasDescription, IPattern by pattern {

  /**
   * 沒有其他語系，就傳中文的 [IPattern.name] 即可
   */
  override fun toString(locale: Locale): String {
    return pattern.name
  }

  override fun getDescription(locale: Locale): String {
    return pattern.notes ?: ""
  }
}
