package destiny.core

import destiny.core.astrology.IAstroPattern
import destiny.core.astrology.IPointAnglePattern
import destiny.core.astrology.IPointHousePattern
import destiny.core.astrology.IPointSignPattern
import java.util.*

/**
 * 整合 [IPattern] , 以及 [Descriptive]
 * 另外新增 list of [Paragraph] 作為段落解說
 */
interface IPatternParasDescription<T : IPattern> : Descriptive, ISource {
  val pattern: T
  val paras: List<Paragraph>
}


data class PatternParasDescription<T : IPattern>(
  override val pattern: T,
  override val paras: List<Paragraph>,
  override val source: String,
  override val author: String? = null
) : IPatternParasDescription<T> {

  /**
   * 沒有其他語系，就傳中文的 [IPattern.getName] 即可
   */
  override fun getTitle(locale: Locale): String {
    return pattern.getName(locale)
  }

  override fun getDescription(locale: Locale): String {
    return pattern.getNotes(locale) ?: ""
  }
}


sealed interface IAstroPatternDescription<T : IAstroPattern> : IPatternParasDescription<T>
interface IPointHouseContent : IAstroPatternDescription<IPointHousePattern>
interface IPointSignContent : IAstroPatternDescription<IPointSignPattern>
interface IPointAspectContent : IAstroPatternDescription<IPointAnglePattern>
